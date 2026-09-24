package bg.vetbook.model;

// Един ред от сметката на прегледа: процедура или медикамент.
public class VisitItem {
    private int id;
    private int visitId;
    private String itemType;
    private String description;
    private double quantity = 1;
    private double unitPrice;

    public int getId() {
        return id;
    }

    public int getVisitId() {
        return visitId;
    }

    public String getItemType() {
        return itemType;
    }

    public String getDescription() {
        return description;
    }

    public double getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setVisitId(int visitId) {
        this.visitId = visitId;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    // Сумата за този ред: количество по единична цена.
    public double getTotal() {
        return quantity * unitPrice;
    }
}
