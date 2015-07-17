#!/bin/sh

WORK_DIR=$1
shift

/usr/bin/python `dirname $0`/installer.py --work-dir=${WORK_DIR} $*
