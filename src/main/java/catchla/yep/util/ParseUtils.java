/*
 * 				Twidere - Twitter client for Android
 * 
 *  Copyright (C) 2012-2014 Mariotaku Lee <mariotaku.lee@gmail.com>
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package catchla.yep.util;

import java.net.MalformedURLException;
import java.net.URL;

public final class ParseUtils {

    public static long parseLong(final String source, final long def) {
        if (source == null) return def;
        try {
            return Long.parseLong(source);
        } catch (final NumberFormatException e) {
            // Wrong number format? Ignore them.
        }
        return def;
    }

    public static String parseString(final Object object) {
        return parseString(object, null);
    }

    public static String parseString(final Object object, final String def) {
        if (object == null) return def;
        return String.valueOf(object);
    }

    public static URL parseURL(final String url_string) {
        if (url_string == null) return null;
        try {
            return new URL(url_string);
        } catch (final MalformedURLException e) {
            // This should not happen.
        }
        return null;
    }

}
