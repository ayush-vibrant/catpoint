module Security { //TODO: Rename using top level package naming
    requires java.desktop;
    requires miglayout;
    requires guava;
    requires gson;
    requires java.prefs;
    requires Image;
    requires java.sql;

    opens org.example.security.data to com.google.gson;
}