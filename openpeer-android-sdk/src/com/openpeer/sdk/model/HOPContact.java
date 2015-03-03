/*******************************************************************************
 *
 *  Copyright (c) 2014 , Hookflash Inc.
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are those
 *  of the authors and should not be interpreted as representing official policies,
 *  either expressed or implied, of the FreeBSD Project.
 *******************************************************************************/
package com.openpeer.sdk.model;

import com.openpeer.javaapi.OPContact;
import com.openpeer.javaapi.OPIdentityContact;

import java.util.ArrayList;
import java.util.List;

public class HOPContact {
    private long mUserId;// locally maintained user id
    private List<HOPIdentity> identities;
    private OPContact mOPContact;

    private String mPeerUri;

    private static HOPContact self;
    public static HOPContact getSelf(){
        return self;
    }
    /**
     * If the user is a contact, or a stranger. This is determined by checking if the contact is
     * associated with any of the user's identity
     * <p/>
     * This is used primarily in group chat to determine if a participant is a known contact.
     *
     * @return
     */
    public boolean isContact() {
        if (identities != null) {
            for (HOPIdentity contact : identities) {
                if (contact.getAssociatedIdentityId() > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Lazy instantiation. OPContact object is needed for creating thread.
     *
     * @return OPContact object
     */
    public OPContact getOPContact() {
        // Lazy creation of opcontact to avoid problem before core stack is ready.
        if (mOPContact == null) {
            HOPIdentity contact = getPreferredContact();

            mOPContact = OPContact.createFromPeerFilePublic(
                HOPAccount.currentAccount().getAccount(),
                contact.getPeerFilePublic());
        }
        return mOPContact;
    }

    public List<OPIdentityContact> getIdentityContacts() {
        List<OPIdentityContact> identityContacts = new ArrayList<>();
        for(HOPIdentity identity:identities){
            identityContacts.add((OPIdentityContact)identity.getContact());
        }
        return identityContacts;
    }

    public void setIdentityContacts(List<HOPIdentity> mIdentityContact) {
        this.identities = mIdentityContact;
    }

    /**
     * Used to construct a new user from incoming thread contact
     *
     * @param contact
     * @param iContacts
     */
    public HOPContact(OPContact contact, List<HOPIdentity> iContacts) {
        this.mOPContact = contact;
        this.identities = iContacts;
        mPeerUri = mOPContact.getPeerURI();
    }

    public HOPContact() {
    }

    /**
     * Get user id that uniquely identitying an openpeer user. This is currently the database
     * record id
     *
     * @return
     */
    public long getUserId() {
        return mUserId;
    }

    public void setUserId(long mUserId) {
        this.mUserId = mUserId;
    }

    /**
     * @return User peer uri
     */
    public String getPeerUri() {
        return mPeerUri;
    }

    public void setPeerUri(String peerUri) {
        this.mPeerUri = peerUri;
    }

    /**
     * Wrapper function to return the peer file public
     *
     * @return
     */
    public String getPeerFilePublic() {
        return getPreferredContact().getPeerFilePublic();
    }

    /**
     * Wrapper function. This returns the name of the preferred identity
     *
     * @return
     */
    public String getName() {
        return getPreferredContact().getName();
    }

    /**
     * Wrapper fucntion. This returns the preferred avatar uri of the prefered identity
     *
     * @return
     */
    public String getAvatarUri() {
        return getPreferredContact().getDefaultAvatarUrl();
    }

    /**
     * priority smaller wins
     * weight bigger wins
     *
     * @return Preferred identity contact based on priority and weight
     */
    HOPIdentity getPreferredContact() {
        if (identities.size() == 1) {
            return identities.get(0);
        } else {
            HOPIdentity preferredContact = identities.get(0);
            for (int i = 1; i < identities.size(); i++) {
                HOPIdentity contact = identities.get(i);
                if (contact.getPriority() < preferredContact.getPriority()) {
                    preferredContact = contact;
                } else if (contact.getPriority() == preferredContact.getPriority() &&
                    contact.getWeight() > preferredContact.getWeight()) {
                    preferredContact = contact;
                }
            }
            return preferredContact;
        }
    }

    /**
     * Find the preferred identity contact of a specific network. This is primarily designed for
     * showing contacts by network. The implementation is not finalized yet.
     * <p/>
     * TODO: implement the function and make it public
     *
     * @return Preferred contact of a specific network,e.g. Facebook,Twitter
     */
    private HOPIdentity getPreferredContactOfNetwork(String network) {
        return identities.get(0);
    }

    public boolean isSame(OPContact contact) {
        return contact.getPeerURI().equals(getOPContact().getPeerURI());
    }

    public boolean isSelf() {
        return mUserId == HOPAccount.selfContactId();
    }

    public void hintAboutLocation(String locationId) {
        getOPContact().hintAboutLocation(locationId);
    }
    public int getNumberOfAssociatedIdentities(){
        return identities.size();
    }
    public List<HOPIdentity> getIdentities(){
        return identities;
    }
    @Override
    public boolean equals(Object o) {
        return o instanceof HOPContact && ((HOPContact) o).getUserId() == this.mUserId;
    }
}
