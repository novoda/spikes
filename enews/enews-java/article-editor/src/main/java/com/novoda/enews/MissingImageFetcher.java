package com.novoda.enews;

import java.util.Random;

class MissingImageFetcher {

    private static final int TOTAL_MISSING_IMAGES = 3;

    private final Random random;

    MissingImageFetcher(Random random) {
        this.random = random;
    }

    String getMissingImageUrl() {
        int i = random.nextInt(TOTAL_MISSING_IMAGES);
        switch (i) {
            case 0:
                return "https://s3-eu-west-1.amazonaws.com/novoda-public-image-bucket/enews-missing-1.png";
            case 1:
                return "https://s3-eu-west-1.amazonaws.com/novoda-public-image-bucket/enews-missing-2.png";
            case 2:
                return "https://s3-eu-west-1.amazonaws.com/novoda-public-image-bucket/enews-missing-3.png";
            default:
                return "https://s3-eu-west-1.amazonaws.com/novoda-public-image-bucket/enews-missing-2.png";
        }
    }
}
