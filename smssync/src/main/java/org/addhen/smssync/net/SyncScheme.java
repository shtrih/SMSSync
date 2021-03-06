/*******************************************************************************
 *  Copyright (c) 2010 - 2013 Ushahidi Inc
 *  All rights reserved
 *  Contact: team@ushahidi.com
 *  Website: http://www.ushahidi.com
 *  GNU Lesser General Public License Usage
 *  This file may be used under the terms of the GNU Lesser
 *  General Public License version 3 as published by the Free Software
 *  Foundation and appearing in the file LICENSE.LGPL included in the
 *  packaging of this file. Please review the following information to
 *  ensure the GNU Lesser General Public License version 3 requirements
 *  will be met: http://www.gnu.org/licenses/lgpl.html.
 *
 * If you have questions regarding the use of this file, please contact
 * Ushahidi developers at team@ushahidi.com.
 ******************************************************************************/
package org.addhen.smssync.net;


import org.addhen.smssync.util.DataFormatUtil;
import org.addhen.smssync.util.Logger;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Class: SyncScheme Description: Specifies a synchronization scheme that formats messages in the
 * way that the sever expects them. Author: Salama A.B. <devaksal@gmail.com>
 */
public class SyncScheme {

    public static enum SyncMethod {POST, PUT}

    ;

    public static enum SyncDataFormat {URLEncoded, JSON, XML, YAML}

    ;

    public static enum SyncDataKey {SECRET, FROM, MESSAGE, SENT_TIMESTAMP, MESSAGE_ID, SENT_TO, DEVICE_ID}

    ;


    private SyncMethod method;

    private SyncDataFormat format;

    private String keySecret;

    private String keyFrom;

    private String keyMessage;

    private String keySentTimeStamp;

    private String keySentTo;

    private String keyMessageID;

    private String keyDeviceID;


    public SyncScheme() {
        init(
                SyncMethod.POST,
                SyncDataFormat.URLEncoded,
                "secret", "from", "message",
                "message_id", "sent_timestamp", "sent_to", "device_id");
    }

    public SyncScheme(String json) {
        try {
            if (!json.contentEquals("")) {
                init(json);
            } else {
                throw new Exception("Empty scheme spec, loading default");
            }
        } catch (Exception ex) {
            Logger.log(SyncScheme.class.getSimpleName(), ex.getMessage());
            //Init default
            init(
                    SyncMethod.POST,
                    SyncDataFormat.URLEncoded,
                    "secret", "from", "message",
                    "message_id", "sent_timestamp", "sent_to", "device_id");
        }
    }

    public SyncScheme(SyncMethod method, SyncDataFormat dataFormat) {
        init(
                method,
                dataFormat,
                "secret", "from",
                "message", "message_id",
                "sent_timestamp", "sent_to", "device_id");
    }

    /**
     * Initialize sync scheme with custom method, data format and keys
     */
    public void init(SyncMethod method, SyncDataFormat dataFormat,
            String kSecret, String kFrom, String kMessage,
            String kMessageID, String kSentTimestamp,
            String kSentTo, String kDeviceID) {
        this.method = method;
        this.format = dataFormat;

        this.keySecret = kSecret;
        this.keyFrom = kFrom;
        this.keySentTimeStamp = kSentTimestamp;
        this.keyMessage = kMessage;
        this.keySentTo = kSentTo;
        this.keyMessageID = kMessageID;
        this.keyDeviceID = kDeviceID;
    }

    /**
     * Initialize sync scheme from json string
     */
    public void init(String json) throws JSONException {
        JSONObject obj = new JSONObject(json);
        this.method = SyncMethod.valueOf(obj.getString("method"));
        this.format = SyncDataFormat.valueOf(obj.getString("dataFormat"));
        this.keySecret = obj.getString("kSecret");
        this.keyFrom = obj.getString("kFrom");
        this.keySentTimeStamp = obj.getString("kSentTimestamp");
        this.keyMessage = obj.getString("kMessage");
        this.keySentTo = obj.getString("kSentTo");
        this.keyMessageID = obj.getString("kMessageID");
        this.keyDeviceID = obj.getString("kDeviceID");

    }

    /**
     * Get the HTTP method the server is expecting
     *
     * @return Http method; POST or PUT
     */
    public SyncMethod getMethod() {
        return method;
    }

    /**
     * Get the data format the server is expecting
     *
     * @return serialization format; JSON, XML, YAML, etc.
     */
    public SyncDataFormat getDataFormat() {
        return format;
    }

    /**
     * Get the mime type of expected data format
     */
    public String getContentType() {
        switch (format) {
            case JSON:
                return "application/json";
            case XML:
                return "application/xml";
            case YAML:
                return "application/yaml";
            default:
                return "application/x-www-form-urlencoded";
        }
    }

    /**
     * Get server expected key for particular data item
     */
    public String getKey(SyncDataKey key) {
        switch (key) {
            case SECRET:
                return keySecret;
            case FROM:
                return keyFrom;
            case MESSAGE:
                return keyMessage;
            case SENT_TIMESTAMP:
                return keySentTimeStamp;
            case MESSAGE_ID:
                return keyMessageID;
            case SENT_TO:
                return keySentTo;
            case DEVICE_ID:
                return keyDeviceID;
            default:
                return "value";
        }
    }

    /**
     * Get string JSON representation of this scheme
     */
    public String toJSONString() {

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("method", method.toString()));
        nameValuePairs.add(new BasicNameValuePair("dataFormat", format.toString()));
        nameValuePairs.add(new BasicNameValuePair("kSecret", keySecret));
        nameValuePairs.add(new BasicNameValuePair("kFrom", keyFrom));
        nameValuePairs.add(new BasicNameValuePair("kSentTimestamp", keySentTimeStamp));
        nameValuePairs.add(new BasicNameValuePair("kMessage", keyMessage));
        nameValuePairs.add(new BasicNameValuePair("kSentTo", keySentTo));
        nameValuePairs.add(new BasicNameValuePair("kMessageID", keyMessageID));
        nameValuePairs.add(new BasicNameValuePair("kDeviceID", keyDeviceID));

        try {
            return DataFormatUtil.makeJSONString(nameValuePairs);
        } catch (JSONException ex) {
            return null;
        }
    }

    @Override
    public String toString() {
        return "SyncScheme {" +
                "method:" + method.toString() +
                ", dataFormat:" + format.toString() +
                ", keys: [" + keyFrom + "," + keySecret + "," + keyMessage + "," + keySentTo + "," +
                keySentTimeStamp + "," + keyMessageID + "," + keyDeviceID + "] " +
                "}";
    }

}
