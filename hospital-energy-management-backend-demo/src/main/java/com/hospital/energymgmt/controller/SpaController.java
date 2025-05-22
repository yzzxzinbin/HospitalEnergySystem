package com.hospital.energymgmt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class SpaController {

    /**
     * Forwards requests made to frontend routes (like /login, /dashboard, etc.)
     * to the main index.html page. This allows the SPA's router to handle them.
     * The regex matches paths that:
     * - Do not start with "api/", "v2/", "swagger-resources/", "swagger-ui/", "webjars/",
     *   "css/", "js/", "img/", "images/", "assets/", "fonts/", "static/".
     * - Are not "favicon.ico".
     * - Do not contain a '.' (dot) in the last path segment (to avoid matching actual files like index.html itself if served from root).
     * It also handles the root path "/".
     */
    @RequestMapping(value = {
            "/", // Handles the root path
            // Matches paths like /login, /dashboard, /user/profile etc.
            // It ensures the path does not contain a '.' (to avoid matching files like some.css)
            // and does not start with any of the excluded prefixes.
            "/{path:^(?!api|v2|swagger-resources|swagger-ui|webjars|css|js|img|images|assets|fonts|static|favicon.ico)[^.]+(?:/[^.]+)*$}/**"
            // The above regex can be broken down:
            // {path:...} - Spring MVC path variable
            // ^(?!...) - Negative lookahead to exclude prefixes
            // [^.]+ - Matches one or more characters that are not a dot (for the first segment)
            // (?:/[^.]+)*$ - Optionally matches further segments, each not containing a dot, until the end of the path part captured by {path}
            // /** - Allows for further subpaths to be handled by the SPA router if necessary, though typically the SPA router works on the path captured by {path}
    })
    public String forwardSpaRoutes(HttpServletRequest request) {
        // Log the path being forwarded for debugging if needed
        // System.out.println("Forwarding SPA route: " + request.getRequestURI());
        return "forward:/index.html";
    }
}
