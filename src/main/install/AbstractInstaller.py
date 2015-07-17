#!/usr/bin/python

""" AbstractInstaller.py -- Installer class/framework

"""

import ConfigParser
import logging
import os
import path
import pickle
import subprocess
import utils

class Validator(object):
    """Valdates a user's answer.

    A map of types to functions is kept. The functions
    validate input of the associated type.
    """
    
    def __init__(self):
        """Create Validator object."""
        
        self.functions = {}
        self.add('null', lambda s: s)
        self.add('int', lambda s: int(s))
        self.add('ip', lambda s: s)
        self.add('path', lambda s: path.path(s))
        self.add('string', lambda s: s)

    def add(self, name, function):
        """Add a new type/function pair to the map."""
        
        self.functions[name]=function

    def test(self, type, value):
        """Assert that the value is correctly formed for the given type and
        return the coerced value. Most of the tests are not strict.

        Example:
          test('int', '44') # checks that '44' is a legal integer, and returns the integer 44.
        """

        return self.functions[type](value)


class Config(object):
    """Interface to Installer's configuration file."""

    def __init__(self, config_filenames, user, home):
        """Read config_filename, populate values"""

        self.config = ConfigParser.SafeConfigParser({"home" : home, "user" : user})
        self.config.read(config_filenames)

    def get_name(self):
        """Return the name of the thing being installed."""
        
        return self.config.get('install', 'name')
    
    def get_memento_filename(self):
        """Return the filename where previous installation answers are saved."""
        
        return self.config.get('install', 'mementoFilename')

    def get_top_dirs(self):
        "Return list of directories under app/."

        return eval(self.config.get('install', 'top_dirs'))
    
    def get_files_to_filter(self):
        "Return list of directories under app/."

        return eval(self.config.get('install', 'files_to_filter'))
    
    def get_questions(self):
        """Return the questions to ask during installation."""

        return eval(self.config.get('install', 'questions'))
    
    def get_constants(self):
        """Return the answers to all of the questions."""
        
        return dict(self.config.items('install_constants'))


    def __str__(self):
        """Return a pretty string representing the configuration."""
        
        result = []
        for section in self.config.sections():
            result.append('[%s]\n' % section)
            for option in self.config.options(section):
                result.append('    %s=%s (%s)\n' % (option, self.config.get(section, option), self.config.get(section, option, True)))

        return ''.join(result)

    

class Questions(object):

    def __init__(self, data, silent, use_saved_defaults, validator):

        self._data = {}
        self._key_types = set()
        self._ordering = []
        
        for key, prompt, type, answer in data:
            self._data[key] = [prompt, type, answer]
            self._key_types.add((key, type))
            self._ordering.append(key)
            
        self.silent = silent
        self.use_saved_defaults = use_saved_defaults
        self._validator = validator

    def ask_question(self, key):
        """Prompts the user to answer a question."""

        prompt, vtype, default_answer = self._data[key]
        
        while 1:
            answer = raw_input(prompt + "[" + str(default_answer) + "]: ");
            if "" == answer:
                answer = default_answer
                
            try:
                self._validator.test(vtype, answer)
                self._data[key][2] = answer
                return
            
            except Exception, e:
                #print e
                print answer, 'is invalid'

    def ask(self):
        """Ask all questions if running in verbose mode (not silent)."""
        
        if not self.silent:
            for key in self._ordering:
                self.ask_question(key)

    def merge(self, questions):
        """Set the given answers."""
        
        for key, (prompt, type, value) in questions._data.items():
            if key in self._data:
                self._data[key][2] = value

    def is_subset(self, old_questions):
        return self._key_types <= old_questions._key_types

    def get_answers(self):
        result = {}

        for key, (prompt, type, value) in self._data.items():
            result[key] = value

        return result


    def __getstate__(self):
        "Pickle get memento"

        odict = self.__dict__.copy()
        del odict['_validator']
        return odict

    def __setstate__(self, memento):
        "Pickle set memento"

        self.__dict__.update(memento)
        self.__dict__['_validator'] = Validator()




class AbstractInstaller(object):
    """Installer Framework, that handles common installation tasks.
    """

    def __init__(self, config_filenames, unpacked_dir=None, silent=False, use_saved_defaults=True):
        """Initialize framework.

        Parameters
        - config_filenames - str - Name of installer's configuration file.
        - home - str - Name of user's home directory. Defaults to HOME environment variable.
        - function - call - Function to handle application specific installation tasks.
        - silent - bool - Try to run silently reusing previous answers. Defaults to False
        - use_saved_defaults - bool - Use previous answers as defaults in prompts,
          otherwise use canned defaults from configuration file.
          """

        home=os.environ['HOME']
        user=os.environ['USER']
        
        logging.info('install() config_files: %s, unpacked_dir: %s', config_filenames, unpacked_dir)

        config = Config(config_filenames, user, home)
        self.application_name = config.get_name();
        self.memento_filename = config.get_memento_filename()
        self.top_dirs = config.get_top_dirs()
        self.files_to_filter = config.get_files_to_filter()
        self.questions = Questions(config.get_questions(), silent, use_saved_defaults, Validator())
        self.constants = config.get_constants()

        self.home = path.path(os.environ['HOME'])
        self.unpacked_dir = unpacked_dir
        self.use_saved_defaults = use_saved_defaults
        
        # load prompts, types, and answers from the previous install.
        # Figure out if we need to ask questions -- for instance if a new
        # question has been added.
        
        previous_questions = self.load()

        if not previous_questions or not self.questions.is_subset(previous_questions):
            logging.info("Current questions don't matched saved questions. Forcing console mode.")
            self.questions.silent = False

        if not previous_questions:
            logging.info("No previous questions were saved. Forcing default question mode.")
            self.questions.use_saved_defaults = False

        logging.info("Options: silent=%s, use_saved_defaults=%s", self.questions.silent, self.questions.use_saved_defaults)
        
        if self.questions.silent:
            logging.info("Silently reuseing saved answers")
            self.questions.merge(previous_questions)

        elif self.questions.use_saved_defaults:
            logging.info("Ask, but use previous answers as defaults")
            self.questions.merge(previous_questions)
            
        else:
            logging.info("Ask, using installer defaults")




    def ask(self):
        """Ask all questions if running in verbose mode (not silent)."""

        self.questions.ask()

    def dump(self):
        """Save the user's answers into the memento file so they can be reused the next
        time the installer is run."""
        
        file = open(self.memento_filename, "w")
        pickle.dump(self.questions, file)
        file.close()

    def load(self):
        """Load the user's previous answers from the memento file."""

        questions = None
        
        try:
            file = open(self.memento_filename, "r")
        except IOError:
            return questions

        try:
            questions = pickle.load(file)
        finally:
            file.close()

        return questions

    def filter(self, source_path, dest_path, permission):
        """Copy source_path to dest_path substituting tokens."""

        mapping = self.questions.get_answers()
        mapping.update(self.constants)
        
        f = file(source_path)
        template = utils.At_Template("".join(f.readlines()))
        f.close()

        dest_path.chmod(0755)
        f = file(dest_path, 'w')
        f.writelines(template.substitute(mapping))
        f.close()
        dest_path.chmod(permission)

    def sudo_args(self):
        trojan_horse = self.home + '/ebs-app-free-hand'
        return ('sudo', trojan_horse)
    
    def sudo(self, *args):
        logging.info('Invoking: ' + " ".join(self.sudo_args() + args))
        subprocess.check_call(self.sudo_args() + args)


    #
    # Higher Level Functions
    #
    
    def recreate_directories(self):
        # Remove existing "app" directory
    
        if self.install_dir.isdir():
            logging.info("Removing pre-existing directory %s.", self.install_dir)
            self.install_dir.rmtree()

        #logging.info("Creating top level directory %s", self.install_dir)
        #self.install_dir.makedirs(0755)

        logging.info('Copying contents of directory bin to %s', self.install_dir)
        path.path.copytree(self.unpacked_dir/'bin', self.install_dir)

  
    def hacks(self):
        pass
        
    def install(self):
        """Do the install."""
        
        self.ask()

        answers = self.questions.get_answers()

        self.install_dir = path.path(answers['install_dir'])
        #self.app_dir = self.install_dir
        #self.app_bin_dir = self.install_dir / 'bin'

        logging.info("Installing %s into directory %s", self.application_name, self.install_dir.realpath())
        logging.info('Installation variables: %s', self.questions.get_answers())

        self.recreate_directories()
        self.hacks()
        self.dump()





    

