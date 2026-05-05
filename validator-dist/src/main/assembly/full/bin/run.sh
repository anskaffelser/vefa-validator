#!/bin/sh

cd $(dirname $(readlink -f $0))/..

java -classpath .:conf:extension/*:lib/* no.dfo.anskaffelser.vefa.validator.dist.Cli $@