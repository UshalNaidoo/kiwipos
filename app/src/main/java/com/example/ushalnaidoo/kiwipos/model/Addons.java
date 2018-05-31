package com.example.ushalnaidoo.kiwipos.model;

import android.support.annotation.NonNull;

public class Addons{

  public enum AddonType {
    PERCENTAGE,
    ACTUAL
  }

  public static Addon createAddon(String id, String itemId, String addonName, String typeInt, String adjustmentAmount) {
    AddonType addonType;
    if (Integer.parseInt(typeInt) == 0) {
      addonType = AddonType.PERCENTAGE;
    }
    else {
      addonType = AddonType.ACTUAL;
    }
    return new Addon(id, itemId, addonName, addonType, adjustmentAmount);
  }

  public static class Addon implements Comparable<Addons.Addon>{
    private String id;
    private String itemId;
    private String addonName;
    private AddonType addonType;
    private String adjustmentAmount;

    Addon(String id, String itemId, String addonName, AddonType addonType, String adjustmentAmount) {
      this.id = id;
      this.itemId = itemId;
      this.addonName = addonName;
      this.addonType = addonType;
      this.adjustmentAmount = adjustmentAmount;
    }

    @Override
    public String toString() {
      return addonName;
    }

    @Override
    public int compareTo(@NonNull Addon addon) {
      if(addon.addonType.ordinal() < this.addonType.ordinal())
        return -1;
      else if(addon.addonType.ordinal() > this.addonType.ordinal())
        return 1;
      else
        return 1;
    }

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getItemId() {
      return itemId;
    }

    public void setItemId(String itemId) {
      this.itemId = itemId;
    }

    public String getAddonName() {
      return addonName;
    }

    public void setAddonName(String addonName) {
      this.addonName = addonName;
    }

    public AddonType getAddonType() {
      return addonType;
    }

    public void setAddonType(AddonType addonType) {
      this.addonType = addonType;
    }

    public String getAdjustmentAmount() {
      return adjustmentAmount;
    }

    public void setAdjustmentAmount(String adjustmentAmount) {
      this.adjustmentAmount = adjustmentAmount;
    }
  }

}
