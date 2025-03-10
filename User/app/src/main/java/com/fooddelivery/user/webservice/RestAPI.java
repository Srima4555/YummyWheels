/* JSON API for android appliation */
package com.fooddelivery.user.webservice;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import org.json.JSONArray;

public class RestAPI {
    private final String urlString = "http://afooddel.hostoise.com/Handler1.ashx";

    private static String convertStreamToUTF8String(InputStream stream) throws IOException {
	    String result = "";
	    StringBuilder sb = new StringBuilder();
	    try {
            InputStreamReader reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[4096];
            int readedChars = 0;
            while (readedChars != -1) {
                readedChars = reader.read(buffer);
                if (readedChars > 0)
                   sb.append(buffer, 0, readedChars);
            }
            result = sb.toString();
		} catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }


    private String load(String contents) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(60000);
        conn.setDoOutput(true);
        conn.setDoInput(true);
        OutputStreamWriter w = new OutputStreamWriter(conn.getOutputStream());
        w.write(contents);
        w.flush();
        InputStream istream = conn.getInputStream();
        String result = convertStreamToUTF8String(istream);
        return result;
    }


    private Object mapObject(Object o) {
		Object finalValue = null;
		if (o.getClass() == String.class) {
			finalValue = o;
		}
		else if (Number.class.isInstance(o)) {
			finalValue = String.valueOf(o);
		} else if (Date.class.isInstance(o)) {
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss", new Locale("en", "USA"));
			finalValue = sdf.format((Date)o);
		}
		else if (Collection.class.isInstance(o)) {
			Collection<?> col = (Collection<?>) o;
			JSONArray jarray = new JSONArray();
			for (Object item : col) {
				jarray.put(mapObject(item));
			}
			finalValue = jarray;
		} else {
			Map<String, Object> map = new HashMap<String, Object>();
			Method[] methods = o.getClass().getMethods();
			for (Method method : methods) {
				if (method.getDeclaringClass() == o.getClass()
						&& method.getModifiers() == Modifier.PUBLIC
						&& method.getName().startsWith("get")) {
					String key = method.getName().substring(3);
					try {
						Object obj = method.invoke(o, null);
						Object value = mapObject(obj);
						map.put(key, value);
						finalValue = new JSONObject(map);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		}
		return finalValue;
	}

    public JSONObject ALogin(String username,String pass) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "ALogin");
        p.put("username",mapObject(username));
        p.put("pass",mapObject(pass));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject AddRest(String name,String cont,String add,String city,String pincode,String cuisine,String latlng,String time,String cost,String logo,String minorder,String type,String status,String mname,String memail,String mcont) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "AddRest");
        p.put("name",mapObject(name));
        p.put("cont",mapObject(cont));
        p.put("add",mapObject(add));
        p.put("city",mapObject(city));
        p.put("pincode",mapObject(pincode));
        p.put("cuisine",mapObject(cuisine));
        p.put("latlng",mapObject(latlng));
        p.put("time",mapObject(time));
        p.put("cost",mapObject(cost));
        p.put("logo",mapObject(logo));
        p.put("minorder",mapObject(minorder));
        p.put("type",mapObject(type));
        p.put("status",mapObject(status));
        p.put("mname",mapObject(mname));
        p.put("memail",mapObject(memail));
        p.put("mcont",mapObject(mcont));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject updateRestaurantDetails(String rid,String name,String cont,String add,String city,String pincode,String cuisine,String latlng,String time,String cost,String logo,String minorder,String type) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "updateRestaurantDetails");
        p.put("rid",mapObject(rid));
        p.put("name",mapObject(name));
        p.put("cont",mapObject(cont));
        p.put("add",mapObject(add));
        p.put("city",mapObject(city));
        p.put("pincode",mapObject(pincode));
        p.put("cuisine",mapObject(cuisine));
        p.put("latlng",mapObject(latlng));
        p.put("time",mapObject(time));
        p.put("cost",mapObject(cost));
        p.put("logo",mapObject(logo));
        p.put("minorder",mapObject(minorder));
        p.put("type",mapObject(type));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject changeRestStatus(String rid,String status) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "changeRestStatus");
        p.put("rid",mapObject(rid));
        p.put("status",mapObject(status));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject DeleteRestaurant(String rid) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "DeleteRestaurant");
        p.put("rid",mapObject(rid));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject getRestaurant(String src,String value) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "getRestaurant");
        p.put("src",mapObject(src));
        p.put("value",mapObject(value));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject getManager(String rid) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "getManager");
        p.put("rid",mapObject(rid));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject UpdateManager(String mid,String name,String email,String cont) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "UpdateManager");
        p.put("mid",mapObject(mid));
        p.put("name",mapObject(name));
        p.put("email",mapObject(email));
        p.put("cont",mapObject(cont));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject getMenu(String rid,String showall,String uid) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "getMenu");
        p.put("rid",mapObject(rid));
        p.put("showall",mapObject(showall));
        p.put("uid",mapObject(uid));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject getReviews(String rid) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "getReviews");
        p.put("rid",mapObject(rid));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject AddDeliveryP(String name,String contact,String email,String city,String pincode) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "AddDeliveryP");
        p.put("name",mapObject(name));
        p.put("contact",mapObject(contact));
        p.put("email",mapObject(email));
        p.put("city",mapObject(city));
        p.put("pincode",mapObject(pincode));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject UpdateDeliveryP(String did,String name,String contact,String email,String city,String pincode) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "UpdateDeliveryP");
        p.put("did",mapObject(did));
        p.put("name",mapObject(name));
        p.put("contact",mapObject(contact));
        p.put("email",mapObject(email));
        p.put("city",mapObject(city));
        p.put("pincode",mapObject(pincode));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject DeleteDeliveryP(String did) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "DeleteDeliveryP");
        p.put("did",mapObject(did));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject getDeliveryP(String src,String value) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "getDeliveryP");
        p.put("src",mapObject(src));
        p.put("value",mapObject(value));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject viewUsers(String src,String value) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "viewUsers");
        p.put("src",mapObject(src));
        p.put("value",mapObject(value));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject viewOrders(String src,String value,String sdate,String edate) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "viewOrders");
        p.put("src",mapObject(src));
        p.put("value",mapObject(value));
        p.put("sdate",mapObject(sdate));
        p.put("edate",mapObject(edate));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject Uregister(String name,String contact,String email,String city,String address,String pincode,String latlng,String pass) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "Uregister");
        p.put("name",mapObject(name));
        p.put("contact",mapObject(contact));
        p.put("email",mapObject(email));
        p.put("city",mapObject(city));
        p.put("address",mapObject(address));
        p.put("pincode",mapObject(pincode));
        p.put("latlng",mapObject(latlng));
        p.put("pass",mapObject(pass));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject Ulogin(String email,String pass) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "Ulogin");
        p.put("email",mapObject(email));
        p.put("pass",mapObject(pass));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject UgetProfile(String uid) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "UgetProfile");
        p.put("uid",mapObject(uid));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject UupdateProfile(String uid,String name,String contact,String email,String city,String address,String pincode,String latlng) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "UupdateProfile");
        p.put("uid",mapObject(uid));
        p.put("name",mapObject(name));
        p.put("contact",mapObject(contact));
        p.put("email",mapObject(email));
        p.put("city",mapObject(city));
        p.put("address",mapObject(address));
        p.put("pincode",mapObject(pincode));
        p.put("latlng",mapObject(latlng));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject UChangePassword(String uid,String oldPass,String newPass) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "UChangePassword");
        p.put("uid",mapObject(uid));
        p.put("oldPass",mapObject(oldPass));
        p.put("newPass",mapObject(newPass));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject Usearchby(String cuisine,String src,String value) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "Usearchby");
        p.put("cuisine",mapObject(cuisine));
        p.put("src",mapObject(src));
        p.put("value",mapObject(value));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject URestdetails(String rid) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "URestdetails");
        p.put("rid",mapObject(rid));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject UAddReviews(String uid,String rid,String review,String rating,String dt) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "UAddReviews");
        p.put("uid",mapObject(uid));
        p.put("rid",mapObject(rid));
        p.put("review",mapObject(review));
        p.put("rating",mapObject(rating));
        p.put("dt",mapObject(dt));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject UrAddFav(String fid,String rid,String uid) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "UrAddFav");
        p.put("fid",mapObject(fid));
        p.put("rid",mapObject(rid));
        p.put("uid",mapObject(uid));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject UAddtoCart(String fid,String food,String price,String quant,String Tprice,String rid,String uid) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "UAddtoCart");
        p.put("fid",mapObject(fid));
        p.put("food",mapObject(food));
        p.put("price",mapObject(price));
        p.put("quant",mapObject(quant));
        p.put("Tprice",mapObject(Tprice));
        p.put("rid",mapObject(rid));
        p.put("uid",mapObject(uid));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject UgetCart(String uid,String rid) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "UgetCart");
        p.put("uid",mapObject(uid));
        p.put("rid",mapObject(rid));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject UupdateCart(String fid,String tid,String quant,String price) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "UupdateCart");
        p.put("fid",mapObject(fid));
        p.put("tid",mapObject(tid));
        p.put("quant",mapObject(quant));
        p.put("price",mapObject(price));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject UdeleteCart(String fid,String tid) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "UdeleteCart");
        p.put("fid",mapObject(fid));
        p.put("tid",mapObject(tid));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject Uplaceorder(String rid,String uid,String address,String city,String pincode,String latlng,String phone,String odate,String otime,String dtime,String payment,ArrayList<String> fid,ArrayList<String> name,ArrayList<String> quan,ArrayList<String> price,String totprice) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "Uplaceorder");
        p.put("rid",mapObject(rid));
        p.put("uid",mapObject(uid));
        p.put("address",mapObject(address));
        p.put("city",mapObject(city));
        p.put("pincode",mapObject(pincode));
        p.put("latlng",mapObject(latlng));
        p.put("phone",mapObject(phone));
        p.put("odate",mapObject(odate));
        p.put("otime",mapObject(otime));
        p.put("dtime",mapObject(dtime));
        p.put("payment",mapObject(payment));
        p.put("fid",mapObject(fid));
        p.put("name",mapObject(name));
        p.put("quan",mapObject(quan));
        p.put("price",mapObject(price));
        p.put("totprice",mapObject(totprice));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject UcancelOrder(String oid,String dt) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "UcancelOrder");
        p.put("oid",mapObject(oid));
        p.put("dt",mapObject(dt));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject UgetOrders(String uid,String src,String date) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "UgetOrders");
        p.put("uid",mapObject(uid));
        p.put("src",mapObject(src));
        p.put("date",mapObject(date));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject UgetDeliveryDetails(String oid) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "UgetDeliveryDetails");
        p.put("oid",mapObject(oid));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject Ureorder(String oid,String rid,String uid) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "Ureorder");
        p.put("oid",mapObject(oid));
        p.put("rid",mapObject(rid));
        p.put("uid",mapObject(uid));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject UgetTransaction(String src,String uid) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "UgetTransaction");
        p.put("src",mapObject(src));
        p.put("uid",mapObject(uid));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject UgetFav(String uid) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "UgetFav");
        p.put("uid",mapObject(uid));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject UremoveFav(String fid,String rid,String uid) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "UremoveFav");
        p.put("fid",mapObject(fid));
        p.put("rid",mapObject(rid));
        p.put("uid",mapObject(uid));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject UgetNotification(String uid) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "UgetNotification");
        p.put("uid",mapObject(uid));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject Mlogin(String email,String pass) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "Mlogin");
        p.put("email",mapObject(email));
        p.put("pass",mapObject(pass));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject MChangePassword(String mid,String oldPass,String newPass) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "MChangePassword");
        p.put("mid",mapObject(mid));
        p.put("oldPass",mapObject(oldPass));
        p.put("newPass",mapObject(newPass));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject MAddMenu(String fname,String desc,String price,String cat,String image,String rid,String isveg) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "MAddMenu");
        p.put("fname",mapObject(fname));
        p.put("desc",mapObject(desc));
        p.put("price",mapObject(price));
        p.put("cat",mapObject(cat));
        p.put("image",mapObject(image));
        p.put("rid",mapObject(rid));
        p.put("isveg",mapObject(isveg));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject MUpdateMenu(String fid,String fname,String desc,String price,String cat,String image,String rid,String isveg,String status) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "MUpdateMenu");
        p.put("fid",mapObject(fid));
        p.put("fname",mapObject(fname));
        p.put("desc",mapObject(desc));
        p.put("price",mapObject(price));
        p.put("cat",mapObject(cat));
        p.put("image",mapObject(image));
        p.put("rid",mapObject(rid));
        p.put("isveg",mapObject(isveg));
        p.put("status",mapObject(status));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject MgetOrders(String rid,String src,String date) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "MgetOrders");
        p.put("rid",mapObject(rid));
        p.put("src",mapObject(src));
        p.put("date",mapObject(date));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject Mchangeorderstatus(String oid,String status) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "Mchangeorderstatus");
        p.put("oid",mapObject(oid));
        p.put("status",mapObject(status));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject MgetDeliveryPerson(String oid,String isdist) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "MgetDeliveryPerson");
        p.put("oid",mapObject(oid));
        p.put("isdist",mapObject(isdist));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject MassignDeliverP_toOrder(String oid,String did) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "MassignDeliverP_toOrder");
        p.put("oid",mapObject(oid));
        p.put("did",mapObject(did));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject MgetTransaction(String src,String date,String rid) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "MgetTransaction");
        p.put("src",mapObject(src));
        p.put("date",mapObject(date));
        p.put("rid",mapObject(rid));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject Dlogin(String email,String pass) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "Dlogin");
        p.put("email",mapObject(email));
        p.put("pass",mapObject(pass));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject Dgetprofile(String src,String value) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "Dgetprofile");
        p.put("src",mapObject(src));
        p.put("value",mapObject(value));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject DUpdateProfile(String did,String name,String contact,String email) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "DUpdateProfile");
        p.put("did",mapObject(did));
        p.put("name",mapObject(name));
        p.put("contact",mapObject(contact));
        p.put("email",mapObject(email));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject DChangePassword(String did,String oldPass,String newPass) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "DChangePassword");
        p.put("did",mapObject(did));
        p.put("oldPass",mapObject(oldPass));
        p.put("newPass",mapObject(newPass));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject DChangestatus(String did,String status) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "DChangestatus");
        p.put("did",mapObject(did));
        p.put("status",mapObject(status));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject DgetOrders(String did,String date,String src) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "DgetOrders");
        p.put("did",mapObject(did));
        p.put("date",mapObject(date));
        p.put("src",mapObject(src));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject DLocationUpdates(String did,String latlng) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "DLocationUpdates");
        p.put("did",mapObject(did));
        p.put("latlng",mapObject(latlng));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

}


