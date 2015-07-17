#!/usr/bin/python

""" installer.py - Converts' installer

"""

import AbstractInstaller
import logging
import logging.config
import optparse
import os
import path
import sys
import utils



class cm_installer(AbstractInstaller.AbstractInstaller):

    def __init__(self, config_files, *args):
        AbstractInstaller.AbstractInstaller.__init__(self, config_files, *args)



def main(argv=None):
    """Install pusher program.

    Use -h switch to see the available options.
    """
    
    if argv is None:
        argv = sys.argv

    logger_filename = utils.execution_path('./logging.properties')
    logging.config.fileConfig(logger_filename)
    

    logging.info('%s Installer %s', '-'*15, '-'*15)
    logging.info('Argv %s', argv)

    usage = "usage: %prog [options]"
    parser = optparse.OptionParser(usage=usage)
    parser.add_option("--work-dir", dest="work_dir", help="Work Directory")
    parser.add_option("-s", "--silent", action="store_true", dest="silent", help="Run silently")
    parser.add_option("-c", "--console", action="store_false", dest="silent", help="Force installer to ask questions")
    parser.add_option("--saved", action="store_true", dest="use_saved_defaults", help="Use saved answers as defaults")
    parser.add_option("--default", action="store_false", dest="use_saved_defaults", help="Use installer answers as defaults")
    parser.add_option("--default-file", dest="default_file", help="Default answer file")
    
    parser.set_defaults(silent=True, use_saved_defaults=True)
    parser.set_defaults(default_file=utils.execution_path('./default.properties'))
    
    (options, args) = parser.parse_args()
    options.work_dir = path.path(options.work_dir)
    options.default_file = path.path(options.default_file)

    config_filename = utils.execution_path('./install.properties')
    installer = cm_installer([config_filename], options.work_dir, options.silent, options.use_saved_defaults)
    installer.install()


        

if __name__ == "__main__":
    sys.exit(main())
