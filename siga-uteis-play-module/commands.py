import sys
import os
import getopt
import shutil

import play.commands.precompile
from play.utils import *
# Here you can create play commands that are specific to the module, and extend existing commands

MODULE = 'siga-uteis-play-module'

# Commands that are specific to your module

COMMANDS = ['siga-uteis-play-module:war']

def execute(**kargs):
    command = kargs.get("command")
    app = kargs.get("app")
    args = kargs.get("args")
    env = kargs.get("env")

   
# This will be executed before any command (new, run...)
def before(**kargs):
    command = kargs.get("command")
    app = kargs.get("app")
    args = kargs.get("args")
    env = kargs.get("env")
    print "siga-uteis-play-module before"
