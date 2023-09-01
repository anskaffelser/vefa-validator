#!/bin/sh

cd $(dirname $(readlink -f $0))/..

exec java -classpath .:conf:extension/*:lib/* no.difi.vefa.validator.Cli $@