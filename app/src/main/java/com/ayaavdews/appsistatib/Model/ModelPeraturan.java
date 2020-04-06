package com.ayaavdews.appsistatib.Model;

public class ModelPeraturan {

    // ============================
    // == Created By :            =
    // == Aya Avdews              =
    // == XII RPL A / 17006892    =
    // == Instagram : @aya_avdews =
    // ============================

    private String kode, jenis_pelanggaran, skor;

    public ModelPeraturan(String kode, String jenis_pelanggaran, String skor) {
        this.kode = kode;
        this.jenis_pelanggaran = jenis_pelanggaran;
        this.skor = skor;
    }

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getJenis_pelanggaran() {
        return jenis_pelanggaran;
    }

    public void setJenis_pelanggaran(String jenis_pelanggaran){
        this.jenis_pelanggaran = jenis_pelanggaran;
    }

    public String getSkor() {
        return skor;
    }

    public void setSkor(String skor){
        this.skor = skor;
    }
}

