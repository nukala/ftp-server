Given that this is a simple-app and not a commercial applications, I've made a few assumptions:

- As required, no libraries. Using only JDK classes (hence some utils+tests may seem excessive). Dependencies are: junit, Mockito and my favorite way for assertions - assert4j.

- Assumes JDK-8 or better

- I've relied heavily on un-caught exceptions to sometimes indicate error codes. In a commercial application, there would be even more graceful error messages. For this purpose - current implementation should suffice.

- Service (response for commands) is handled by a cached-threadpool. It grows to OS maximum allowed and shrinks when threads are idle (default=60s). There have been numerous articles that talk about throughput excellence of blocking IO vs non-blocking. If this turns to be a problem -- write a NonblockingFtpServer class and use it.

- I used Community-IntelliJ (free) and IJ comes with a built-in code-coverage tool. Its' code-coverage snapshot is included here as a png.

- jdk8+ string concatenation is highly performant. It rivals string-builder's performance.

- If we encounter numerous excessively slow clients (take too much time to send the command), then there is a chance of thread exhaustion (since there are only so many threads in the cachedPool). Then consder using a separate thread pool for readers. That way fast-clients will get some service too.

- For the first version, there will be no caching and no-recursive folder lookup. INDEX is equal to `ls *.txt`

- Usage: ClientApp command [options]. So each invokation is independent of the other. Kinda like what we expect out off an HTTP-Server. Here you make ONE client-app-execution for index. Then a series of executions to get the files you want. Each execution is for one command ONLY.

- Included a wierdPerformanceTest in the EndToEndIT class to demonstrate a version of load-test/performance-measurement.

BUILD (requires jdk8):
  > mvn package 
SERVER:
  > java -jar target\ftpsrvr-1.0-SNAPSHOT.jar  SOME_FOLDER
CLIENT:
  > java -cp target\classes com.ravi.ftp.app.ClientApp COMMAND
