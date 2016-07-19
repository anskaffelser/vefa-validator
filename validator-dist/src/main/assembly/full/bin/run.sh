#!/bin/sh

cd $(dirname $(readlink -f $0))/..

java -classpath .:conf:extension/*:lib/* no.difi.vefa.validator.dist.Cli $@