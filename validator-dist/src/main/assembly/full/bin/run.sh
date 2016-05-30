#!/bin/sh

cd $(dirname $(readlink -f $0))/..

java -classpath .:conf:lib/* no.difi.vefa.validator.dist.Cli $@