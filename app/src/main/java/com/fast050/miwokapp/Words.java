package com.fast050.miwokapp;

class Words
{
    private String MiwokWord;
    private String DefaultTranslationWork;
    private Integer imageResource;
    private Integer soundResources;



    public Words(Integer soundResources,String miwokWord, String defaultTranslationWork, Integer imageResource) {
        MiwokWord = miwokWord;
        DefaultTranslationWork = defaultTranslationWork;
        this.imageResource = imageResource;
        this.soundResources = soundResources;
    }

    public Words(String miwokWord, String defaultTranslationWork, int imageResource) {
        MiwokWord = miwokWord;
        DefaultTranslationWork = defaultTranslationWork;
        this.imageResource=imageResource;
    }

    public Words(Integer soundResources,String miwokWord, String defaultTranslationWork) {
        MiwokWord = miwokWord;
        DefaultTranslationWork = defaultTranslationWork;
        this.soundResources = soundResources;
    }

    public String getMiwokWord() {
        return MiwokWord;
    }

    public String getDefaultTranslationWork() {
        return DefaultTranslationWork;
    }

    public Integer getImageResource()
    {
        return imageResource;
    }

    public Integer getSoundResources() {
        return soundResources;
    }
}
