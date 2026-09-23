package bg.vetbook.model;
//Класът представляващ собственик на животно
public class Owner {
    private int id;
    private String name;
    private String phone;
    private String email;
    //Конструктор взимайки всички полета
    public Owner(int id, String name, String phone, String email){
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    public String getPhone(){
        return phone;
    }
    public void setPhone(String phone){
        this.phone = phone;
    }

    public String getEmail(){
        return email;
    }
    public void setTEmail(String email){
        this.email = email;
    }

    public String toString(){
        return name; // Помага за визуализация в списъка
    }
}
