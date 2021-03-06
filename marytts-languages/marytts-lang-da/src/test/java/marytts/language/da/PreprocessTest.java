/**
 *Copyright (C) 2003 DFKI GmbH. All rights reserved.
 */
package marytts.language.da;

import marytts.language.da.Preprocess;
import marytts.util.dom.DomUtils;

import org.custommonkey.xmlunit.*;
import org.testng.Assert;
import org.testng.annotations.*;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;

/**
 * @author Tristan Hamilton + HB
 *
 *
 */
public class PreprocessTest {

    private static Preprocess module;

    @BeforeSuite
    public static void setUpBeforeClass() {
	module = new Preprocess();
    }

    @DataProvider(name = "DocData")
    private Object[][] numberExpansionDocData() {
	// @formatter:off
	return new Object[][] { { "1", "en" }
				,{ "2", "to" } 
				,{ "3", "tre" }
				,{ "4", "fire" }
				//{ "43", "fyrtio tre"}
				//,{ "1e", "förste"}
				//,{ "2e", "andre" }
				//,{ "3e", "tredje" }
				//,{ "5e", "femte" } 
	};
	// @formatter:on
    }

    @DataProvider(name = "NumExpandData")
    private Object[][] numberExpansionDocDataCardinal() {
	// @formatter:off
	return new Object[][] { { "1", "en" }, 
				{ "2", "to" }, 
				{ "3", "tre" }, 
				{ "4", "fire" },
				{ "5", "fem"} };
	// @formatter:on
    }

    @DataProvider(name = "OrdinalExpandData")
    private Object[][] numberExpansionDocDataOrdinal() {
	// @formatter:off
	return new Object[][] { { "2", "andre" },
				{ "3", "tredje" },
				{ "4", "fjärde" } };
	// @formatter:on
    }

    @Test(dataProvider = "DocData")
    public void testSpellout(String tokenised, String expected) throws Exception, ParserConfigurationException, SAXException,
								       IOException {
	Document tokenisedDoc;
	Document expectedDoc;
	String tokens = "<maryxml xmlns=\"http://mary.dfki.de/2002/MaryXML\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" version=\"0.5\" xml:lang=\"da\"><p><s><t>"
	    + tokenised + "</t></s></p></maryxml>";
	tokenisedDoc = DomUtils.parseDocument(tokens);
	String words = "<maryxml xmlns=\"http://mary.dfki.de/2002/MaryXML\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" version=\"0.5\" xml:lang=\"da\"><p><s><mtu orig=\""
	    + tokenised + "\"><t>" + expected + "</t></mtu></s></p></maryxml>";
	expectedDoc = DomUtils.parseDocument(words);
	module.checkForNumbers(tokenisedDoc);
	Diff diff = XMLUnit.compareXML(expectedDoc, tokenisedDoc);

	//HB
	if (diff.identical() == false) {
	    System.err.println("tokenised: "+tokenised+", expected: "+expected);
	    System.err.println("tokenisedDoc: "+DomUtils.document2String(tokenisedDoc));
	    System.err.println("expectedDoc: "+DomUtils.document2String(expectedDoc));
	    System.err.println("diff.identical(): "+diff.identical());
	}

	Assert.assertTrue(diff.identical());
    }

    @Test(dataProvider = "NumExpandData")
    public void testExpandNum(String token, String word) {
	double x = Double.parseDouble(token);
	String actual = module.expandNumber(x);
	Assert.assertEquals(actual, word);
    }

    
    // @Test(dataProvider = "OrdinalExpandData")
    // public void testExpandOrdinal(String token, String word) {
    // 	double x = Double.parseDouble(token);
    // 	String actual = module.expandOrdinal(x);
    // 	Assert.assertEquals(actual, word);
    // }
}
