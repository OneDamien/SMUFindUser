package ADCrawler;

import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;

/**
 * The purpose of this class is to receive useful information from active directory
 */

public class ADCrawler {

    private String emailAddress, fname, lname, username;

public ADCrawler(ActiveDirectory ad, String queryField, String queryType) {
        try {
            //Get information from active directory for the s#
            NamingEnumeration<SearchResult> result = ad.searchUser(queryField,
                    queryType, "SMUNET.SMU.CA");
            if (result.hasMore()) {
                //Get the search results
                SearchResult rs = result.next();
                Attributes attrs = rs.getAttributes();

                //Save email address
                emailAddress = attrs.get("mail").toString();
                emailAddress = emailAddress.substring(emailAddress.indexOf(":") + 1);

                //Save client's name
                fname = attrs.get("givenname").toString();
                fname = fname.substring(fname.indexOf(":") + 1);

                //Save client's name
                lname = attrs.get("sn").toString();
                lname = lname.substring(lname.indexOf(":") + 1);

                username = attrs.get("cn").toString();
                username = username.substring(username.indexOf(":") + 1);

            } else {
                System.out.println("No search result found!");
            }
        } catch (javax.naming.NamingException ignored) {
        } catch (NullPointerException ignored) {
        }
    ad.closeLdapConnection();
    }

    public String getFname() {
        return fname;
    }
    public String getLname() {
        return lname;
    }
    public String getUsername() { return username; }
    public String getEmailAddress() {
        return emailAddress;
    }

}


