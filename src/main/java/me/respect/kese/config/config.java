package me.respect.kese.config;

import me.respect.kese.Kese;

public class config {
    Kese main;
    public config(Kese main) {
        this.main = main;
        this.respect();
    }

    private void respect() {
        this.main.config.options().copyDefaults(true);
        this.main.saveConfig();
    }
}
