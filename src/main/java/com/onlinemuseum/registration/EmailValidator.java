package com.onlinemuseum.registration;

import org.springframework.stereotype.Service;

import java.util.function.Predicate;
import java.util.regex.*;

@Service
public class EmailValidator implements Predicate<String> {
    /**
     * (?=.{6,30}@)            # local-part, length min 6 max 30
     *
     * [A-Za-z0-9_-]+          # Start with chars in the bracket [ ], one or more (+)
     *                         # dot (.) not in the bracket[], it can't start with a dot (.)
     *
     * (\\.[A-Za-z0-9_-]+)*	   # follow by a dot (.), then chars in the bracket [ ] one or more (+)
     *                         # * means this is optional
     *                         # this rule for two dots (.)
     *
     * @                       # must contain a @ symbol
     *
     * [^-]                    # domain can't start with a hyphen (-)
     *
     * [A-Za-z0-9-]+           # Start with chars in the bracket [ ], one or more (+)
     *
     * (\\.[A-Za-z0-9-]+)*     # follow by a dot (.), optional
     *
     * (\\.[A-Za-z]{2,8})      # the last tld, chars in the bracket [ ], length min 2, max 8
     */
    public static final Pattern VALID_EMAIL_PATTERN =
            Pattern.compile("^(?=.{6,30}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+" +
                    "(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,8})$", Pattern.CASE_INSENSITIVE);

    @Override
    public boolean test(String s) {
        Matcher matcher = VALID_EMAIL_PATTERN.matcher(s);
        return matcher.matches();
    }
}
