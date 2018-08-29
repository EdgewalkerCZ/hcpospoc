#!/bin/bash
echo "start app provision via file"
whoami
whoami

echo $USER

mkvirtualenv --python=python3 test1
lsvirtualenv
#sudo -u vagrant bash -c 'mkvirtualenv test3'

echo "end app provision"
