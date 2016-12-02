#!/bin/sh

if [ -e target ]; then rm -r target; fi

if [ ! -e ubl-2.0-source.zip ]; then
	echo "* Fetching source"
	wget -q http://docs.oasis-open.org/ubl/os-UBL-2.0.zip -O ubl-2.0-source.zip
fi

build=$(md5sum ubl-2.0-source.zip | cut -d " " -f 1)

echo "* Unzip source"
unzip -q ubl-2.0-source.zip os-UBL-2.0/xsdrt/* -d target

cd target/os-UBL-2.0/xsdrt

echo "* Generate buildconfig.xml"

echo '<?xml version="1.0" encoding="UTF-8"?>' >> buildconfig.xml
echo '<buildConfigurations xmlns="http://difi.no/xsd/vefa/validator/1.0">' >> buildconfig.xml

for xsd in maindoc/*.xsd; do
	filename=$(basename $xsd | sed "s:\.xsd::")

	echo "\t<configuration>" >> buildconfig.xml
	echo "\t\t<identifier alias=\"$(echo $filename | tr '[:upper:]' '[:lower:]' | cut -d '-' -f 2)\">$(echo $filename | tr '[:upper:]' '[:lower:]')</identifier>" >> buildconfig.xml
	echo "\t\t<title>$(echo $filename | sed 's:\-: :g')</title>" >> buildconfig.xml
	echo "\t\t<file type=\"xml.xsd\" path=\"maindoc/$filename.xsd\" />" >> buildconfig.xml
	echo "\t</configuration>" >> buildconfig.xml
done

for xsd in common/*.xsd; do
    filename=$(basename $xsd | sed "s:\.xsd::")

    echo "\t<include path=\"common/$filename.xsd\" />" >> buildconfig.xml
done

echo "</buildConfigurations>" >> buildconfig.xml

# Ready for build