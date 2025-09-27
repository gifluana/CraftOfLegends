package com.lunazstudios.craftoflegends.stats;

import net.minecraft.nbt.NbtCompound;

public class PlayerStats {
    // Integers
    private int health;              // Vida
    private int mana;                // Mana
    private int level;               // Level
    private int xp;                  // XP
    private int abilityPoints;       // Ability points to spend
    private int gold;                // Gold
    private int attackDamage;        // AD
    private int abilityPower;        // AP
    private int armor;               // Armor
    private int magicResist;         // MR
    private int abilityHaste;        // Haste
    private int moveSpeed;           // Move speed (kept as int per your rule)

    // Floats
    private float attackSpeed;
    private float critChance;

    public PlayerStats() {
        this.health = 100;
        this.mana = 100;
        this.level = 1;
        this.xp = 0;
        this.abilityPoints = 0;
        this.gold = 0;

        this.attackDamage = 60;
        this.abilityPower = 0;
        this.armor = 30;
        this.magicResist = 30;
        this.abilityHaste = 0;
        this.moveSpeed = 345;

        this.attackSpeed = 0.66f;
        this.critChance = 0f;
    }

    // --- getters/setters ---
    public int getHealth() { return health; }
    public void setHealth(int v) { this.health = v; }

    public int getMana() { return mana; }
    public void setMana(int v) { this.mana = v; }

    public int getLevel() { return level; }
    public void setLevel(int v) { this.level = v; }

    public int getXp() { return xp; }
    public void setXp(int v) { this.xp = v; }

    public int getAbilityPoints() { return abilityPoints; }
    public void setAbilityPoints(int v) { this.abilityPoints = v; }

    public int getGold() { return gold; }
    public void setGold(int v) { this.gold = v; }

    public int getAttackDamage() { return attackDamage; }
    public void setAttackDamage(int v) { this.attackDamage = v; }

    public int getAbilityPower() { return abilityPower; }
    public void setAbilityPower(int v) { this.abilityPower = v; }

    public int getArmor() { return armor; }
    public void setArmor(int v) { this.armor = v; }

    public int getMagicResist() { return magicResist; }
    public void setMagicResist(int v) { this.magicResist = v; }

    public int getAbilityHaste() { return abilityHaste; }
    public void setAbilityHaste(int v) { this.abilityHaste = v; }

    public int getMoveSpeed() { return moveSpeed; }
    public void setMoveSpeed(int v) { this.moveSpeed = v; }

    public float getAttackSpeed() { return attackSpeed; }
    public void setAttackSpeed(float v) { this.attackSpeed = v; }

    public float getCritChance() { return critChance; } // 0–100 (%)
    public void setCritChance(float v) { this.critChance = v; }

    // --- NBT ---
    public NbtCompound toNbt() {
        NbtCompound tag = new NbtCompound();
        tag.putInt("health", health);
        tag.putInt("mana", mana);
        tag.putInt("level", level);
        tag.putInt("xp", xp);
        tag.putInt("abilityPoints", abilityPoints);
        tag.putInt("gold", gold);

        tag.putInt("attackDamage", attackDamage);
        tag.putInt("abilityPower", abilityPower);
        tag.putInt("armor", armor);
        tag.putInt("magicResist", magicResist);
        tag.putInt("abilityHaste", abilityHaste);
        tag.putInt("moveSpeed", moveSpeed);

        tag.putFloat("attackSpeed", attackSpeed);
        tag.putFloat("critChance", critChance);
        return tag;
    }

    public void fromNbt(NbtCompound tag) {
        if (tag.contains("health")) this.health = tag.getInt("health");
        if (tag.contains("mana")) this.mana = tag.getInt("mana");
        if (tag.contains("level")) this.level = tag.getInt("level");
        if (tag.contains("xp")) this.xp = tag.getInt("xp");
        if (tag.contains("abilityPoints")) this.abilityPoints = tag.getInt("abilityPoints");
        if (tag.contains("gold")) this.gold = tag.getInt("gold");

        if (tag.contains("attackDamage")) this.attackDamage = tag.getInt("attackDamage");
        if (tag.contains("abilityPower")) this.abilityPower = tag.getInt("abilityPower");
        if (tag.contains("armor")) this.armor = tag.getInt("armor");
        if (tag.contains("magicResist")) this.magicResist = tag.getInt("magicResist");
        if (tag.contains("abilityHaste")) this.abilityHaste = tag.getInt("abilityHaste");
        if (tag.contains("moveSpeed")) this.moveSpeed = tag.getInt("moveSpeed");

        if (tag.contains("attackSpeed")) this.attackSpeed = tag.getFloat("attackSpeed");
        if (tag.contains("critChance")) this.critChance = tag.getFloat("critChance");
    }
}