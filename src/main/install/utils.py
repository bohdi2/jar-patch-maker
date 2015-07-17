"""utils.py - A collection of application utilities."""

import ConfigParser
import inspect
import os
import path
import string
import sys

__all__ = [
           "get_csv",
           "get_directories",
           "makedirs",
           "execution_path",
           "At_Template"
           ]


def get_csv(config, section, key):
    """Read a property from a config file, converting it to a list."""
    
    return [x.strip() for x in config.get(section, key).split(',')]


def get_directories(config, section, key):
    """Read a propert from a config file, converting to a list of pathnames"""
    
    return [path.path(x) for x in get_csv(config, section, key)]
    

    
def makedirs(dirname, mode=0777):
    """Make a file directory returning a path. Like mkdir -p."""
    
    p = path.path(dirname)
    if not p.isdir():
        p.makedirs(mode)
        p.chmod(mode)

    return p
    



def execution_path(filename):
  """Return a path relative to this Python file."""
  
  frame_file = inspect.getfile(sys._getframe(1))
  return path.path(os.path.join(os.path.dirname(frame_file), filename))


class At_Template(string.Template):
    """Substitute anything inside double @'s"""

    pattern = r"""
    (?P<escaped>@{2})     |              # Escape sequence of two delimiters
    @(?P<named>[_a-z][_a-z0-9]*)@ |      # delimiter and a Python identifier
    @{(?P<braced>[_a-z][_a-z0-9]*)@} #|   # delimiter and a Python identifier
    #(?P<invalid>@)                       # Other ill-formed delimiter exprs
    """
    



