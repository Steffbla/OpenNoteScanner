package com.todobom.opennotescanner.helpers;

public interface AppConstants {

    String DOCUMENTS_EXTRA_KEY = "documents";
    String FILE_SUFFIX_PDF = ".pdf";
    String FILE_SUFFIX_JPG = ".jpg";
    String FILE_SUFFIX_PNG = ".png";
    String[] FILE_FORMAT_VALUES = new String[]{FILE_SUFFIX_PDF, FILE_SUFFIX_JPG, FILE_SUFFIX_PNG};

    String DEFAULT_PAGE_SIZE = "A4";
    String[] PAGE_SIZE_VALUES = new String[]{"A3", DEFAULT_PAGE_SIZE, "A4_landscape", "A5"};

    String DRACOON = "dracoon";
    String NEXTCLOUD = "nextcloud";
    String EMAIL = "email";
    String FTP_SERVER = "ftp_server";
    String LOCAL = "local";
    String[] SAVE_OPTION_VALUES = new String[]{DRACOON, NEXTCLOUD, EMAIL, FTP_SERVER, LOCAL};

    String DEFAULT_FOLDER_NAME = "OpenNoteScanner";
}
