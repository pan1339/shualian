package com.dajing.jsoup;

import java.io.IOException;

import org.json.JSONObject;

import com.dajing.util.PropertiesUtil;

public interface JsoupService {

	/**
	 * 
	 * @param postJsonData
	 * @throws IOException
	 */

	public void execute(JSONObject postJsonData) throws IOException;

	/**
	 * 
	 * @param propertiesPath
	 * @param formatStrs
	 * @throws IOException
	 */
	public JSONObject executeFromProperties(String propertiesPath, String... formatStrs) throws IOException;

	/**
	 * 
	 * @param jsonData
	 */
	public void post(JSONObject postJsonData) throws IOException;

	/**
	 * 
	 * @param jsonData
	 */
	public void get(JSONObject postJsonData) throws IOException;

	/**
	 * 从配置文件中初始化PostJsonData
	 * 
	 * @return
	 * @throws IOException
	 */
	public JSONObject initPostJsonData(String propertiesPath);

	/**
	 * 从默认配置文件中初始化PostJsonData
	 * 
	 * @return
	 * @throws IOException
	 */
	public JSONObject initPostJsonData();

	/**
	 * 读取pathName路径下的所有properties文件
	 * 
	 * @param pathName
	 * @return
	 */
	public JSONObject initAllPostJsonData(String pathName);

	/**
	 * 
	 * DjsoupService封装key,用户封装发送的请求字段
	 * 
	 * @author Administrator
	 *
	 */
	public class DJsoupKey {
		/**
		 * 请求的url链接
		 * <P/>
		 * request url String
		 */
		public final static String REQUEST_URL = "REQUEST_URL";

		/**
		 * 请求method方法
		 * <P/>
		 * request method String
		 */
		public final static String REQUEST_METHOD = "REQUEST_METHOD";

		/**
		 * 请求头,封装在JsonObject,需要转成HashMap传入Connection.headers(),或遍历传入Connection.header(key,value);
		 * <p/>
		 * request headers JsonObject
		 */
		public final static String REQUEST_HEADERS = "HEADERS";

		/**
		 * 请求的内容
		 */
		public final static String REQUEST_DATAS = "DATAS";

		/**
		 * 请求cookies,封装在JsonObject,需要转成HashMap传入Connection.cookies(),或遍历传入Connection.cookie(key,value);
		 * request REQUEST_COOKIES JsonObject
		 */
		public final static String REQUEST_COOKIES = "REQUEST_COOKIES";

		/**
		 * 请求的一些状态,封装在JsonObject,
		 * 包括REQUEST_IGNORE_CONTENT_TYPE/REQUEST_FOLLOW_REDIRECTS/REQUEST_IGNORE_HTTP_ERRORS/REQUEST_POST_DATA_CHARSET
		 * request REQUEST_CONNECT_STATUS JsonObject
		 */
		public final static String REQUEST_CONNECT_STATUS = "CONNS";

		/**
		 * 忽略cookies boolean
		 */
		// public final static String IGNORE_COOKIES = "IGNOGE_COOKIES";

		/**
		 * 请求返回headers,封装在JsonObject response headers JsonObject
		 */
		public final static String RESPONSE_HEADERS = "RESPONSE_HEADERS";

		/**
		 * 
		 * response body String
		 */
		public final static String RESPONSE_BODY = "RESPONSE_BODY";

		/**
		 * 忽略cookies boolean
		 */
		// public final static String IGNORE_COOKIES = "IGNOGE_COOKIES";

		/**
		 * 请求返回cookies,封装在JsonObject response cookies JsonObject
		 */
		public final static String RESPONSE_COOKIES = "COOKIES";

		/**
		 * 
		 * 设置请求内容
		 */
		public final static String REQUEST_AS_BODY = "REQUEST_AS_BODY";

		/**
		 * 请求超时时间,默认30*1000s
		 * <P/>
		 * request REQUEST_TIMEOUT int
		 */
		public final static String REQUEST_TIMEOUT = "REQUEST_TIMEOUT";

		/**
		 * 忽略response返回类型 boolean jsoup默认为false
		 * </p>
		 * By default this is false
		 * </p>
		 * Ignore the document's Content-Type when parsing the response. an
		 * unrecognised content-type will cause an IOException to be thrown.
		 * (This is to prevent producing garbage by attempting to parse a JPEG
		 * binary image, for example.) Set to true to force a parse attempt
		 * regardless of content type.
		 */
		public final static String REQUEST_IGNORE_CONTENT_TYPE = "REQUEST_IGNORE_CONTENT_TYPE";

		/**
		 * 重定向 boolean jsoup默认为true
		 * 
		 * </p>
		 * By default this is true.
		 * </p>
		 * Configures the connection to (not) follow server redirects.
		 */
		public final static String REQUEST_FOLLOW_REDIRECTS = "REQUEST_FOLLOW_REDIRECTS";

		/**
		 * 忽略http errors
		 * 
		 * </p>
		 * By default this is false;
		 * </p>
		 * Configures the connection to not throw exceptions when a HTTP error
		 * occurs. (4xx - 5xx, e.g. 404 or 500). an IOException is thrown if an
		 * error is encountered. If set to true, the response is populated with
		 * the error body, and the status message will reflect the error.
		 */
		public final static String REQUEST_IGNORE_HTTP_ERRORS = "REQUEST_IGNORE_HTTP_ERRORS";

		/**
		 * 
		 * postDataCharset character set to encode post data
		 * </p>
		 * Sets the default post data character set for x-www-form-urlencoded
		 * post data
		 */
		public final static String REQUEST_POST_DATA_CHARSET = "REQUEST_POST_DATA_CHARSET";
	}

	/**
	 * 
	 * Requests部分
	 * <p/>
	 * Header 部分
	 */
	public class DReqHeader {
		public final static String[] headerNames = { //
				"Accept", // 0
				"Accept-Charset", // 1
				"Accept-Encoding", // 2
				"Accept-Language", // 3
				"Accept-Ranges", // 4
				"Authorization", // 5
				"Cache-Control", // 6
				"Connection", // 7
				"Cookie", // 8
				"Content-Length", // 9
				"Content-Type", // 10
				"Date", // 11
				"Expect", // 12
				"From", // 13
				"Host", // 14
				"If-Match", // 15
				"If-Modified-Since", // 16
				"If-None-Match", // 17
				"If-Range", // 18
				"If-Unmodified-Since", // 19
				"Max-Forwards", // 20
				"Pragma", // 21
				"Proxy-Authorization", // 22
				"Range", // 23
				"Referer", // 24
				"TE", // 25
				"Upgrade", // 26
				"User-Agent", // 27
				"Via", // 28
				"Warning",// 29
		};

		/**
		 * 指定客户端能够接收的内容类型 //Accept: text/plain, text/html
		 */
		public final static int Accept = 0;

		/**
		 * 浏览器可以接受的字符编码集.//Accept-Charset: iso-8859-5
		 */
		public final static int Accept_Charset = 1;

		/**
		 * 指定浏览器可以支持的web服务器返回内容压缩编码类型. //Accept-Encoding: compress, gzip
		 */
		public final static int Accept_Encoding = 2;
		/**
		 * 浏览器可接受的语言 Accept-Language: en,zh
		 */
		public final static int Accept_Language = 3;
		/**
		 * 可以请求网页实体的一个或者多个子范围字段 //Accept-Ranges: bytes
		 */
		public final static int Accept_Ranges = 4;

		/**
		 * HTTP授权的授权证书 //Authorization: Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==
		 */
		public final static int Authorization = 5;

		/**
		 * 指定请求和响应遵循的缓存机制 //Cache-Control: no-cache
		 */
		public final static int Cache_Control = 6;

		/**
		 * 表示是否需要持久连接.(HTTP 1.1默认进行持久连接) //Connection: close
		 */
		public final static int Connection = 7;

		/**
		 * HTTP请求发送时,会把保存在该请求域名下的所有cookie值一起发送给web服务器. //Cookie: $Version=1;
		 * Skin=new;
		 */
		public final static int Cookie = 8;

		/**
		 * 请求的内容长度 Content-Length: 348
		 */
		public final static int Content_Length = 9;

		/**
		 * 请求的与实体对应的MIME信息 Content-Type: application/x-www-form-urlencoded
		 */
		public final static int Content_Type = 10;

		/**
		 * 请求发送的日期和时间 Date: Tue, 15 Nov 2010 08:12:31 GMT
		 */
		public final static int Date = 11;

		/**
		 * 请求的特定的服务器行为 Expect: 100-continue
		 */
		public final static int Expect = 12;

		/**
		 * 发出请求的用户的Email From: user@email.com
		 */
		public final static int From = 13;

		/**
		 * 指定请求的服务器的域名和端口号 Host: www.zcmhi.com
		 */
		public final static int Host = 14;

		/**
		 * 只有请求内容与实体相匹配才有效 If-Match: “737060cd8c284d8af7ad3082f209582d”
		 */
		public final static int If_Match = 15;

		/**
		 * 如果请求的部分在指定时间之后被修改则请求成功，未被修改则返回304代码 If-Modified-Since: Sat, 29 Oct
		 * 2010 19:43:31 GMT
		 */
		public final static int If_Modified_Since = 16;

		/**
		 * 如果内容未改变返回304代码，参数为服务器先前发送的Etag，与服务器回应的Etag比较判断是否改变 If-None-Match:
		 * “737060cd8c284d8af7ad3082f209582d”
		 */
		public final static int If_None_Match = 17;

		/**
		 * 如果实体未改变，服务器发送客户端丢失的部分，否则发送整个实体。参数也为Etag If-Range:
		 * “737060cd8c284d8af7ad3082f209582d”
		 */
		public final static int If_Range = 18;

		/**
		 * 只在实体在指定时间之后未被修改才请求成功 If-Unmodified-Since: Sat, 29 Oct 2010 19:43:31
		 * GMT
		 */
		public final static int If_Unmodified_Since = 19;

		/**
		 * 限制信息通过代理和网关传送的时间 Max-Forwards: 10
		 */
		public final static int Max_Forwards = 20;

		/**
		 * 用来包含实现特定的指令 Pragma: no-cache
		 */
		public final static int Pragma = 21;

		/**
		 * 连接到代理的授权证书 Proxy-Authorization: Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==
		 */
		public final static int Proxy_Authorization = 22;

		/**
		 * 只请求实体的一部分，指定范围 Range: bytes=500-999
		 */
		public final static int Range = 23;

		/**
		 * 先前网页的地址，当前请求网页紧随其后,即来路 Referer: http://www.zcmhi.com/archives/71.html
		 */
		public final static int Referer = 24;

		/**
		 * 客户端愿意接受的传输编码，并通知服务器接受接受尾加头信息 TE: trailers,deflate;q=0.5
		 */
		public final static int TE = 25;

		/**
		 * 向服务器指定某种传输协议以便服务器进行转换（如果支持） Upgrade: HTTP/2.0, SHTTP/1.3, IRC/6.9,
		 * RTA/x11
		 */
		public final static int Upgrade = 26;

		/**
		 * User-Agent的内容包含发出请求的用户信息 User-Agent: Mozilla/5.0 (Linux; X11)
		 */
		public final static int User_Agent = 27;

		/**
		 * 通知中间网关或代理服务器地址，通信协议 Via: 1.0 fred, 1.1 nowhere.com (Apache/1.1)
		 */
		public final static int Via = 28;

		/**
		 * 关于消息实体的警告信息 Warn: 199 Miscellaneous warning
		 */
		public final static int Warning = 29;
	}

	/**
	 * 响应头封装
	 * 
	 * @author Administrator
	 *
	 */
	public class DResHeader {
		public final static String[] headerNames = { //
				"Accept-Ranges", // 0
				"Age", // 1
				"Allow", // 2
				"Cache-Control", // 3
				"Content-Encoding", // 4
				"Content-Language", // 5
				"Content-Length", // 6
				"Content-Location", // 7
				"Content-MD5", // 8
				"Content-Range", // 9
				"Content-Type", // 10
				"Date", // 11
				"ETag", // 12
				"Expires", // 13
				"Last-Modified", // 14
				"Location", // 15
				"Pragma", // 16
				"Proxy-Authenticate", // 17
				"refresh", // 18
				"Retry-After", // 19
				"Server", // 20
				"Set-Cookie", // 21
				"Trailer", // 22
				"Transfer-Encoding", // 23
				"Vary", // 24
				"Via", // 25
				"Warning", // 26
				"WWW-Authenticate", // 27
		};
		/**
		 * 表明服务器是否支持指定范围请求及哪种类型的分段请求 Accept-Ranges: bytes
		 */
		public final static int Accept_Ranges = 0;
		/**
		 * 从原始服务器到代理缓存形成的估算时间（以秒计，非负） Age: 12
		 */
		public final static int Age = 1;

		/**
		 * 对某网络资源的有效的请求行为，不允许则返回405 Allow: GET, HEAD
		 */
		public final static int Allow = 2;

		/**
		 * 告诉所有的缓存机制是否可以缓存及哪种类型 Cache-Control: no-cache
		 */
		public final static int Cache_Control = 3;

		/**
		 * web服务器支持的返回内容压缩编码类型。 Content-Encoding: gzip
		 */
		public final static int Content_Encoding = 4;

		/**
		 * 响应体的语言 Content-Language: en,zh
		 */
		public final static int Content_Language = 5;

		/**
		 * 响应体的长度 Content-Length: 348
		 */
		public final static int Content_Length = 6;

		/**
		 * 请求资源可替代的备用的另一地址 Content-Location: /index.htm
		 */
		public final static int Content_Location = 7;

		/**
		 * 返回资源的MD5校验值 Content-MD5: Q2hlY2sgSW50ZWdyaXR5IQ==
		 */
		public final static int Content_MD5 = 8;

		/**
		 * 在整个返回体中本部分的字节位置 Content-Range: bytes 21010-47021/47022
		 */
		public final static int Content_Range = 9;

		/**
		 * 返回内容的MIME类型 Content-Type: text/html; charset=utf-8
		 */
		public final static int Content_Type = 10;

		/**
		 * 原始服务器消息发出的时间 Date: Tue, 15 Nov 2010 08:12:31 GMT
		 */
		public final static int Date = 11;

		/**
		 * 请求变量的实体标签的当前值 ETag: “737060cd8c284d8af7ad3082f209582d”
		 */
		public final static int ETag = 12;

		/**
		 * 响应过期的日期和时间 Expires: Thu, 01 Dec 2010 16:00:00 GMT
		 */
		public final static int Expires = 13;

		/**
		 * 请求资源的最后修改时间 Last-Modified: Tue, 15 Nov 2010 12:45:26 GMT
		 */
		public final static int Last_Modified = 14;

		/**
		 * 用来重定向接收方到非请求URL的位置来完成请求或标识新的资源 Location:
		 * http://www.zcmhi.com/archives/94.html
		 */
		public final static int Location = 15;

		/**
		 * 包括实现特定的指令，它可应用到响应链上的任何接收方 Pragma: no-cache
		 */
		public final static int Pragma = 16;

		/**
		 * 它指出认证方案和可应用到代理的该URL上的参数 Proxy-Authenticate: Basic
		 */
		public final static int Proxy_Authenticate = 17;

		/**
		 * 应用于重定向或一个新的资源被创造，在5秒之后重定向（由网景提出，被大部分浏览器支持）
		 * 
		 * Refresh: 5; url= http://www.zcmhi.com/archives/94.html
		 */
		public final static int refresh = 18;

		/**
		 * 如果实体暂时不可取，通知客户端在指定时间之后再次尝试 Retry-After: 120
		 */
		public final static int Retry_After = 19;

		/**
		 * web服务器软件名称 Server: Apache/1.3.27 (Unix) (Red-Hat/Linux)
		 */
		public final static int Server = 20;

		/**
		 * 设置Http Cookie Set-Cookie: UserID=JohnDoe; Max-Age=3600; Version=1
		 */
		public final static int Set_Cookie = 21;

		/**
		 * 指出头域在分块传输编码的尾部存在 Trailer: Max-Forwards
		 */
		public final static int Trailer = 22;

		/**
		 * 文件传输编码 Transfer-Encoding:chunked
		 */
		public final static int Transfer_Encoding = 23;

		/**
		 * 告诉下游代理是使用缓存响应还是从原始服务器请求 Vary: *
		 */
		public final static int Vary = 24;

		/**
		 * 告知代理客户端响应是通过哪里发送的 Via: 1.0 fred, 1.1 nowhere.com (Apache/1.1)
		 */
		public final static int Via = 25;
		/**
		 * 警告实体可能存在的问题 Warning: 199 Miscellaneous warning
		 */
		public final static int Warning = 26;

		/**
		 * 表明客户端请求实体应该使用的授权方案 WWW-Authenticate: Basic
		 */
		public final static int WWW_Authenticate = 27;

	}

}
