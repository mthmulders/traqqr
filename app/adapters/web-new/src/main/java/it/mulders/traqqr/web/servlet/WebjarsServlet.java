package it.mulders.traqqr.web.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Jakarta-based, simplified version of the WebjarsServlet that comes with
 * <a href="http://github.com/webjars/webjars-servlet-2.x">webjars-servlet-2.x</a>.
 *
 * The webjars-servlet-2.x version is built against the <pre>javax.servlet</pre> package,
 * so it doesn't work in Jakarta 11. This modified version reduces functionality to only
 * that what Traqqr needs, and is built against the <pre>jakarta.servlet</pre> package.
 */
public class WebjarsServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(WebjarsServlet.class);

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        var webjarsURI = request.getRequestURI().replaceFirst(request.getContextPath(), "");
        var webjarsResourceURI = "/META-INF/resources" + webjarsURI;
        log.debug("WebJar resource requested; uri={}", webjarsResourceURI);

        if (isDirectoryRequest(webjarsResourceURI)) {
            log.info("WebJar directory request denied");
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        try (var inputStream = this.getClass().getResourceAsStream(webjarsResourceURI);) {
            if (inputStream == null) {
                log.info("Non-existing WebJar resource; resource_uri={}", webjarsResourceURI);
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            var filename = getFileName(webjarsResourceURI);
            var mimeType = this.getServletContext().getMimeType(filename);

            response.setContentType(mimeType != null ? mimeType : "application/octet-stream");
            copy(inputStream, response.getOutputStream());
        }
    }

    private static boolean isDirectoryRequest(String uri) {
        return uri.endsWith("/");
    }

    private String getFileName(String webjarsResourceURI) {
        var tokens = webjarsResourceURI.split("/");
        return tokens[tokens.length - 1];
    }

    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
    private static final int EOF = -1;

    private static void copy(InputStream input, OutputStream output) throws IOException {
        int n = 0;
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
    }
}
