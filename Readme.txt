PROXY SERVER

In this part you will extend your stage 1 program to become a full web proxy, a program that, as discussed in class, reads a request from a browser, forwards that request to a web server, reads the reply from the web server, and forwards the reply back to the browser. Your program should be able to handle many connections, one at a time, one following the other. (They are not concurrent connections.) Your program will also have a ‘blocking’ part which means web requests to certain black listed web sites will not be returned.

2.1 Requirements

Your program will speak a subset of the HTTP/1.1 protocol, which is defined in RFC 2616. You're only responsible for a small subset of HTTP/1.1. Your proxy should satisfy these requirements:

Your program should emit (to standard output) a startup line as well as ONE log line for each request it processes or blocks. (See the next section for examples.)
GET requests work and images and arbitrary length binary files are transferred correctly.
Your program should properly handle Full-Requests (RFC 1945 section 4.1) up to 65535 bytes. You may close the connection if a Full-Request is larger than that.
You must support URLs with a numerical IP address also (e.g. http://128.181.0.31/).
You are not allowed to use fork() or threads or multiprocesses of any kind.
You may not allocate more than 30MB of memory or have more than 32 file descriptors.
Your program should service each request as HTTP would i.e. with a response. No crash / segmentation / hang or the like. If there is a truly non-recoverable error your program should call exit(1).
A list of ‘blocked’ web sites will be supplied: requests to these sites should not be fulfilled, instead an error page will be returned.
Your program should be maximum 300 lines (excluding comments.)
You do not have to worry about POST, non HTTP URL/s or HTTP-headers (RFC section 4.2) for now.
Please see Moodle Announcements for the revised Java / C / AFS requirements

2.2 DESIGN

You must use / extend the dns() function or method you used in part 1. Language switch e.g. C to Java is not permitted at this stage. Your program should have, including dns(), the following functions/methods:

doParse() -- to scan and parse the browser request

doHTTP() -- to actually transfer the file

Your main loop should be clear and readable i.e. it should call doParse(), then dns(), and then wrap around doHTTP(). Note that these function names are required and if they are not present exactly you may lose points.

Note that design will count towards your overall grade. Poor design (e.g. using goto or too many loops or too much extraneous code or even radical changes to the dns() code from part 1) may cost you up to 50% of the grade even if your program otherwise works flawlessly.

2.3 UAIP The standard University Academic Integrity Policy applies (please remember the "zero tolerance" discussion in class). Violations will result in a grade of zero for the project and second-time violations will receive an F in the class.

That said -- we STRONGLY ENCOURAGE TO ASK US if you have questions about using a piece of code or logic. In some cases the answer may be no - but in some other cases e.g. Integer.parseInt() you may be permitted to use it.

2.4 RUNNING

This will be the same as before:

$ ./stage2 2000                       ## listens on port 2000
stage 2 program by (group-leader-UCID) listening on port (2000)
REQUEST: http://qprober.cs.columbia.edu/publications/...  ## URL Requested
Ctl-C                                 ## how we’ll stop it

Note carefully the startup line (printed once on startup to standard out) which should indicate the group number and the group leader’s email. All requests should be logged with a REQUEST line on standard output.

Submit / Deliverables

Each team should submit a single file (web.c or Web.java) that will be compiled and run as described above. The comment block with the group's names is mandatory as before. We will test using a dozen or so requests. Tests will be done by using the browser proxy settings. Roughly speaking programs that pass all tests will receive full credit, and partial tests will receive partial credit on a sliding scale. Do be careful to test your program on all the error cases or corner cases you can think of - a crash or hang can cost you many/all points.

There may be an intermediate deliverable due on 10/26 (next week)!

Grading / Testing

This project will be worth 20% of your class grade.

We will test your proxy using one or more of the following tests:

Ordinary fetch            Fetch a normal web page.
Split request               Request in more than one ‘chunk’ (connection)
Large request/resp      Requests of size 65535 bytes or response of size about 200MB
Bad Connect               URL to a bad port or bad host (e.g. blahblah.com:2222)
Malformed request     HTTP request that is syntactically incorrect
Huge request               Requests larger than 65535 bytes (of up to 1MB)
Stress test                   We will test your proxy with several high-speed requests

We may use some combination of these tests, including some unspecified tests that are within HTTP protocol. (Think of this as a demo to your ‘startup’ venture capitalist for next round funding. Just as at a startup, you will have to design your own tests to mirror how we will test your code.) Your final grade will depend on your design and the performance of your code. Roughly speaking, proxies that pass our tests and remain standing will earn full marks, each failed test gets a 5-10% deduction, proxies that don’t even do Ordinary Fetch will get zero.

Happy programming!

KXM
