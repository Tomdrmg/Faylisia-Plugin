package fr;

public class Utilisateur {
    private final String nom;
    private int health;

    public Utilisateur(String nom, int health) {
        this.nom = nom;
        this.health = health;
    }

    public String getNom() {
        return nom;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getHealth() {
        return health;
    }

    public void attack(Utilisateur target) {
        target.takeDamage(6);

        System.out.println(nom + " a attaqué " + target.nom + ", il " + (target.health == 0 ? "est mort" : "a " + target.health + "hp"));
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0) health = 0;
    }

    @Override
    public String toString() {
        return "{Nom: " + nom + ", Health: " + health + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Utilisateur u) return nom.equals(u.nom) && health == u.health;

        return false;
    }
}
