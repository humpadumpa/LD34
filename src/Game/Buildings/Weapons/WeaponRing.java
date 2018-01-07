/*
 * WeaponTree.java
 *
 * Dec 24, 2017
 */
package Game.Buildings.Weapons;

/**
 * 
 * @author Per Eresund
 */
public class WeaponRing {
	private Weapon[][] weapons;
	private int size;
	private int capacity;
	private int layers;
	
	{
		size = 0;
		layers = 1;
		capacity = 4;
		weapons = new Weapon[layers][capacity];
	}
	
	private int getLayerSize(int layer) {
		if (layer == 0) return 4;
		else return (int)(4 * Math.pow(2, layer-1));
	}
	
	private void createNewLayer() {
		Weapon[][] oldWeapons = weapons;
		weapons = new Weapon[++layers][];
		
		System.arraycopy(oldWeapons, 0, weapons, 0, layers-1);
		weapons[layers-1] = new Weapon[capacity];
		
		capacity *= 2;
	}
	
	private void addAtPos(Weapon w, int layer, int index) {
		weapons[layer][index] = w;
	}
	
	private int[] findFreePos() {
		for (int layer = 0; layer < layers; layer++) {
			int layerSize = getLayerSize(layer);
			for (int index = 0; index < layerSize; index++) {
				if (weapons[layer][index] == null) {
					return new int[] {
						layer, index
					};
				}
			}
		}
		
		throw new IndexOutOfBoundsException();
	}
	
	public int[] addWeapon(Weapon w) {
		size++;
		int[] freePos;
		
		if (size > capacity) {
			createNewLayer();
			freePos = new int[] {
				layers-1, 0
			};
		} else {
			freePos = findFreePos();
		}
		
		addAtPos(w, freePos[0], freePos[1]);
		return freePos;
	}
	
	private int[] findWeapon(Weapon w) {
		int[] pos = {-1, -1};
		
		for (int layer = 0; layer < layers; layer++) {
			int layerSize = getLayerSize(layer);
			for (int index = 0; index < layerSize; index++) {
				if (w == weapons[layer][index]) {
					pos[0] = layer;
					pos[1] = index;
				};
			}
		}
		
		return pos;
	}
	
	public void removeWeapon(Weapon w) {
		int[] weaponPos = findWeapon(w);
		if (weaponPos[0] == -1 || weaponPos[1] == -1) return;
		
		weapons[weaponPos[0]][weaponPos[1]] = null;
		size--;
	}
	
	public double getWeaponRadians(int layer, int index) {
		int layerSize = getLayerSize(layer);
		
		int numerator = 2 * index + (layer == 0 ? 0 : 1);
		int denominator = layerSize*2;
		double fraction = (double)numerator / (double)denominator;
		
		double radians = 2D * Math.PI * fraction;
		return radians;
	}
	
	public double getWeaponRadians(Weapon w) {
		int[] weaponPos = findWeapon(w);
		if (weaponPos[0] == -1 || weaponPos[1] == -1) return -1;
		
		return getWeaponRadians(weaponPos[0], weaponPos[1]);
	}
}