#!/bin/sh

# installer.sh
#
# Self Extracting Archive Script used by maven binset2 plugin
#

#
# Create temp directory
#
WORK_DIR=/tmp/selfextract.$$
THIS_DIR=`dirname $0`
THIS_FILE=`cd $THIS_DIR && pwd`/`basename $0`

export WORK_DIR
mkdir ${WORK_DIR}

SKIP=`awk '/^__ARCHIVE_FOLLOWS__/ { print NR +1; exit 0; }' $THIS_FILE`

#
# Take the TGZ portion of this file and pipe it to tar
#
tail --lines=+${SKIP} $THIS_FILE | gunzip | tar -C ${WORK_DIR} -xf -

#
# execute the user's installation script
#

chmod u+x ${WORK_DIR}/install/installer.sh
${WORK_DIR}/install/installer.sh ${WORK_DIR} $*

#
# delete the temp files
#

rm -rf ${WORK_DIR}

exit 0

__ARCHIVE_FOLLOWS__
