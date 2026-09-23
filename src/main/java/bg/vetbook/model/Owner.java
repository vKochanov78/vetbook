package bg.vetbook.model;
//Класът представляващ собственик на животно
public class Owner {
    private int id;
    private String ime;
    private String telefon;
    private String email;
    //Конструктор взимайки всички полета
    public Owner(int id,String ime, String telefon, String email){
        this.id = id;
        this.ime = ime;
        this.telefon = telefon;
        this.email = email;
    }

    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }

    public String getIme(){
        return ime;
    }
    public void setIme(String ime){
        this.ime = ime;
    }

    public String getTelefon(){
        return telefon;
    }
    public void setTelefon(String telefon){
        this.telefon = telefon;
    }

    public String getEmail(){
        return email;
    }
    public void setTEmail(String email){
        this.email = email;
    }

    public String toString(){
        return ime; // Помага за визуализация в списъка
    }
}
