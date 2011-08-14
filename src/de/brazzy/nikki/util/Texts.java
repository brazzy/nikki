package de.brazzy.nikki.util;

/*   
 *   Copyright 2010 Michael Borgwardt
 *   Part of the Nikki Photo GPS diary:  http://www.brazzy.de/nikki
 *
 *  Nikki is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Nikki is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Nikki.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * Holds internationalized texts.
 * 
 * @author Michael Borgwardt
 * 
 */
public final class Texts {
    private static final ResourceBundle bundle = ResourceBundle.getBundle(
            "de.brazzy.nikki.util.Texts", new ResourceControl());

    public static final String ERROR_PREFIX = bundle
            .getString("general.error_prefix");

    public static class Main {
        public static final String TITLE = bundle.getString("mainwindow.title");
        public static final String DIRECTORIES = bundle
                .getString("mainwindow.directories");
        public static final String DAYS = bundle.getString("mainwindow.days");
        public static final String IMAGES = bundle
                .getString("mainwindow.images");
        public static final String UNKNOWN_DAY = bundle
                .getString("mainwindow.unknown_day");
        public static final String ADD_BUTTON = bundle
                .getString("mainwindow.add_button");
        public static final String ADD_TOOLTIP = bundle
                .getString("mainwindow.add_tooltip");
        public static final String DELETE_BUTTON = bundle
                .getString("mainwindow.delete_button");
        public static final String DELETE_TOOLTIP = bundle
                .getString("mainwindow.delete_tooltip");
        public static final String SCAN_BUTTON = bundle
                .getString("mainwindow.scan_button");
        public static final String SCAN_TOOLTIP = bundle
                .getString("mainwindow.scan_tooltip");
        public static final String SAVE_BUTTON = bundle
                .getString("mainwindow.save_button");
        public static final String SAVE_TOOLTIP = bundle
                .getString("mainwindow.save_tooltip");
        public static final String GEOTAG_BUTTON = bundle
                .getString("mainwindow.geotag_button");
        public static final String GEOTAG_TOOLTIP = bundle
                .getString("mainwindow.geotag_tooltip");
        public static final String EXPORT_BUTTON = bundle
                .getString("mainwindow.export_button");
        public static final String EXPORT_TOOLTIP = bundle
                .getString("mainwindow.export_tooltip");
        public static final String HELP_TOOLTIP = bundle
                .getString("mainwindow.help_tooltip");
        public static final String EXPORT_ALL_BUTTON = bundle
                .getString("mainwindow.export_all_button");
        public static final String EXPORT_NONE_BUTTON = bundle
                .getString("mainwindow.export_none_button");
        public static final String EXPORT_TEXT = bundle
                .getString("mainwindow.export_text");

    }

    public static class Image {
        public static final String ORDERED_BY_FILENAME = bundle
                .getString("image.sortedby.filename");
        public static final String ORDERED_BY_TIME = bundle
                .getString("image.sortedby.time");
        public static final String FILE = bundle.getString("image.file");
        public static final String TIME = bundle.getString("image.time");
        public static final String LATITUDE = bundle
                .getString("image.latitude");
        public static final String LONGITUDE = bundle
                .getString("image.longitude");
        public static final String OFFSET_ERROR_TITLE = bundle
                .getString("image.offset_error_title");
        public static final String DIFF_TOOLTIP = bundle
                .getString("image.diff_tooltip");
        public static final String EXPORT = bundle.getString("image.export");
        public static final String COPY_TOOLTIP = bundle
                .getString("image.copy_tooltip");
        public static final String PASTE_TOOLTIP = bundle
                .getString("image.paste_tooltip");
        public static final String OFFSETFINDER_TOOLTIP = bundle
                .getString("image.offsetfinder_tooltip");
        public static final String EXPORT_TOOLTIP = bundle
                .getString("image.export_tooltip");
        public static final String EXPORT_LOCKED_TOOLTIP = bundle
                .getString("image.export_locked_tooltip");
    }

    public static class Dialogs {
        public static final String CONFIRM_TITLE = bundle
                .getString("dialogs.confirm_title");
        public static final String ERROR_TITLE = bundle
                .getString("dialogs.error_title");
        public static final String UNSAVED_MESSAGE = bundle
                .getString("dialogs.unsaved_message");

        public static class Export {
            public static final String NODATA_MESSAGE = bundle
                    .getString("dialogs.export.nodata_message");
            public static final String NOIMAGE_MESSAGE = bundle
                    .getString("dialogs.export.noimage_message");
            public static final String ERROR_PREFIX = bundle
                    .getString("dialogs.export.error_prefix");
            public static final String PROGRESS_HEADER = bundle
                    .getString("dialogs.export.progress_header");
        }

        public static class Save {
            public static final String ERROR_PREFIX = bundle
                    .getString("dialogs.save.error_prefix");
            public static final String ERROR_SAVE_CLOSE = bundle
                    .getString("dialogs.save.error_save_close");
            public static final String PROGRESS_HEADER = bundle
                    .getString("dialogs.save.progress_header");
        }

        public static class Scan {
            public static final String ERROR_PREFIX = bundle
                    .getString("dialogs.scan.error_prefix");
            public static final String PROGRESS_HEADER = bundle
                    .getString("dialogs.scan.progress_header");
        }

        public static class About {

            public static final String TITLE = bundle
                    .getString("dialogs.about.title");
            public static final String FILENAME = bundle
                    .getString("dialogs.about.filename");

        }

        public static class ScanOptions {

            public static final String TITLE = bundle
                    .getString("dialogs.scan_option.title");
            public static final String TIMEZONE_LABEL = bundle
                    .getString("dialogs.scan_option.timezone_label");

        }

        public static class GeotagOptions {

            public static final String TITLE = bundle
                    .getString("dialogs.geotag_option.title");
            public static final String OFFSET_LABEL = bundle
                    .getString("dialogs.geotag_option.offset_label");

        }
    }

    /**
     * Enables the loading of property files encoded in UTF-8
     * 
     * @author Michael Borgwardt
     */
    private static class ResourceControl extends ResourceBundle.Control {
        @Override
        public ResourceBundle newBundle(String baseName, Locale locale,
                String format, ClassLoader loader, boolean reload)
                throws IllegalAccessException, InstantiationException,
                IOException {
            String bundlename = toBundleName(baseName, locale);
            String resName = toResourceName(bundlename, "properties");
            InputStream stream = loader.getResourceAsStream(resName);
            return new PropertyResourceBundle(new InputStreamReader(stream,
                    "UTF-8"));
        }

    }
}
