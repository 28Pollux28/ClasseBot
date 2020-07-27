package fr.pollux28.classbot;

import org.apache.commons.configuration2.XMLConfiguration;

public class XMLConfig {
	XMLConfiguration configCreate = new XMLConfiguration();
    configCreate.setFileName("settings.xml");
    configCreate.addProperty("somesetting", "somevalue");
    configCreate.save();
}
