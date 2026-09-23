package bg.vetbook.model;

public class Animal {
    private int id;
    private String ime;
    private String vid;
    private int ownerId;
    private String OwnerIme;

    public Animal(int id,String ime, String vid, int ownerId){
        this.id= id;
        this.ime= ime;
        this.vid = vid;
        this.ownerId= ownerId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOwnerId(){
        return ownerId;
    }
    public void setOwnerId(int ownerId){
        this.ownerId= ownerId;
    }

    public String getOwnerIme() {
        return OwnerIme;
    }

    public void setOwnerIme(String OwnerIme) {
        this.OwnerIme = OwnerIme;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getIme() {
        return ime;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }
}
