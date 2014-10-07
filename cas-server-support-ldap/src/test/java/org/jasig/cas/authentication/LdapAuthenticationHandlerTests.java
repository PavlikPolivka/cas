/*
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.cas.authentication;

import org.jasig.cas.adaptors.ldap.AbstractLdapTests;
import org.junit.Test;
import org.ldaptive.LdapEntry;

import javax.security.auth.login.FailedLoginException;

import static org.junit.Assert.*;

/**
 * Unit test for {@link LdapAuthenticationHandler}.
 *
 * @author Marvin S. Addison
 */
public class LdapAuthenticationHandlerTests extends AbstractLdapTests {

    private LdapAuthenticationHandler handler;

    public LdapAuthenticationHandlerTests() {
        super("/ldap-context.xml", "/authn-context.xml");

        this.handler = this.context.getBean("ldapAuthenticationHandler", LdapAuthenticationHandler.class);
    }

    @Test
    public void testAuthenticateSuccess() throws Exception {
        for (final LdapEntry entry : this.getEntries()) {
            final String username = getUsername(entry);
            final String psw = entry.getAttribute("userPassword").getStringValue();
            final HandlerResult result = this.handler.authenticate(
                    new UsernamePasswordCredential(username, psw));
            assertNotNull(result.getPrincipal());
            assertEquals(username, result.getPrincipal().getId());
            assertEquals(
                    entry.getAttribute("displayName").getStringValue(),
                    result.getPrincipal().getAttributes().get("displayName"));
            assertEquals(
                    entry.getAttribute("mail").getStringValue(),
                    result.getPrincipal().getAttributes().get("mail"));
        }
    }

    @Test(expected=FailedLoginException.class)
    public void testAuthenticateFailure() throws Exception {
        for (final LdapEntry entry : this.getEntries()) {
            final String username = getUsername(entry);
            this.handler.authenticate(new UsernamePasswordCredential(username, "badpassword"));
            fail("Should have thrown FailedLoginException.");

        }
    }

    @Test(expected=FailedLoginException.class)
    public void testAuthenticateNotFound() throws Exception {
        this.handler.authenticate(new UsernamePasswordCredential("notfound", "somepwd"));
        fail("Should have thrown FailedLoginException.");
    }
}
