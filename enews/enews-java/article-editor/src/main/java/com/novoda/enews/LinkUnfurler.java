package com.novoda.enews;

import com.larvalabs.linkunfurl.LinkInfo;
import com.larvalabs.linkunfurl.LinkUnfurl;

import java.io.IOException;

interface LinkUnfurler {

    Information unfurl(String url) throws IOException;

    class Information {
        private final String title;
        private final String originalUrl;

        public Information(String title, String originalUrl) {
            this.title = title;
            this.originalUrl = originalUrl;
        }

        public String getTitle() {
            return title;
        }

        public String getOriginalUrl() {
            return originalUrl;
        }
    }

    class Http implements LinkUnfurler {

        @Override
        public Information unfurl(String url) throws IOException {
            LinkInfo info = LinkUnfurl.unfurl(url, 3500);
            return new Information(info.getTitle(), url);
        }
    }

    class Mock implements LinkUnfurler {

        @Override
        public Information unfurl(String url) {
            return new Information("Mock Title", url);
        }
    }

}
