// MediaNotFoundException.java
package com.globaledge.academy.lms.media.exception;

public class MediaNotFoundException extends RuntimeException {
    public MediaNotFoundException(String message) {
        super(message);
    }
}