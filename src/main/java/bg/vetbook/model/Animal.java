package bg.vetbook.model;

public class Animal {
    private int id;
    private String name;
    private String species;
    private int owner_id;
    private String ownerName;

    public Animal(int id, String name, String species, int owner_id){
        this.id= id;
        this.name = name;
        this.species = species;
        this.owner_id = owner_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOwner_id(){
        return owner_id;
    }
    public void setOwner_id(int owner_id){
        this.owner_id = owner_id;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }
}
