/**
 * 
 */
package fr.nivcoo.pointz.inventory;

import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class Inventory {
	public static final String TICK = "tick";
	private HashMap<String, Object> values;
	private Player player;
	private InventoryProvider inventoryProvider;
	private int size;

	private List<Integer> excluseCases;
	private ClickableItem[] items;
	private org.bukkit.inventory.Inventory bukkitInventory;

	public Inventory(Player player, InventoryProvider inventoryProvider, Consumer<Inventory> params) {
		this.values = new HashMap<>();
		this.player = player;
		this.inventoryProvider = inventoryProvider;
		params.accept(this);
		this.excluseCases = inventoryProvider.excluseCases(this);
		this.size = inventoryProvider.rows(this);
		this.items = new ClickableItem[9 * size];
		this.bukkitInventory = Bukkit.createInventory(player, size * 9, inventoryProvider.title(this));
		put(TICK, 0);
	}

	public Player getPlayer() {
		return player;
	}

	public InventoryProvider getInventoryProvider() {
		return inventoryProvider;
	}

	public org.bukkit.inventory.Inventory getBukkitInventory() {
		return bukkitInventory;
	}

	public int getRows() {
		return size;
	}

	public List<Integer> getExcludeCases() {
		return excluseCases;
	}

	public void set(int col, int row, ClickableItem item) {
		if (col < 1 || col > 9)
			throw new IllegalArgumentException("col must be between 1 and 9 but is " + col);
		if (row < 1 || row > getRows())
			throw new IllegalArgumentException("row must be between 1 and " + getRows());
		set(locToPos(col, row), item);
	}

	public void set(int pos, ClickableItem item) {
		if (pos < 0 || pos > size * 9 - 1)
			throw new IllegalArgumentException("pos must be between 0 and " + (size * 9 - 1) + ", but is " + pos);
		items[pos] = item;
		bukkitInventory.setItem(pos, item.getItemStack());
	}

	public void fill(ClickableItem item) {
		for (int row = 0; row < size; row++)
			for (int col = 0; col < 9; col++)
				set(row * 9 + col, item);
	}

	public void rectangle(int col, int row, int width, int height, ClickableItem item) {
		if (col < 1 || col > 9)
			throw new IllegalArgumentException("col must be between 1 and 9");
		if (row < 1 || row > 6)
			throw new IllegalArgumentException("row must be between 1 and the maximum number of rows");
		// 10 - col because width starts with 1 and not 0
		if (width < 1 || width > 10 - col)
			throw new IllegalArgumentException("The width must be between 1 and " + (10 - col));
		if (height < 1 || height > getRows() + 1 - col)
			throw new IllegalArgumentException("The height must be between 1 and " + (getRows() + 1 - col));
		rectangle(locToPos(col, row), width, height, item);
	}

	public void rectangle(int pos, int width, int height, ClickableItem item) {
		if (pos < 0 || pos > size * 9)
			throw new IllegalArgumentException("pos must be between 0 and " + (size * 9) + ", but is " + pos);
		int[] colRow = posToLoc(pos);
		int col = colRow[0];
		int row = colRow[1];
		if (col < 1 || col > 9)
			throw new IllegalArgumentException("col must be between 1 and 9, but is " + col);
		if (row < 1 || row > 6)
			throw new IllegalArgumentException("row must be between 1 and the maximum number of rows, but is " + row);
		// 10 - col because width starts with 1 and not 0
		if (width < 1 || width > 10 - col)
			throw new IllegalArgumentException("The width must be between 1 and " + (10 - col) + ", but is " + width);
		if (height < 1 || height > size + 1 - row)
			throw new IllegalArgumentException(
					"The height must be between 1 and " + (size + 1 - row) + ", but is " + height);
		for (int i = col; i < col + width; i++)
			for (int j = row; j < row + height; j++)
				// Around
				if (i == col || i == col + width - 1 || j == row || j == row + height - 1)
					set(i, j, item);
	}

	public void fillRectangle(int col, int row, int width, int height, ClickableItem item) {
		if (col < 1 || col > 9)
			throw new IllegalArgumentException("col must be between 1 and 9, but is " + col);
		if (row < 1 || row > 6)
			throw new IllegalArgumentException("row must be between 1 and the maximum number of rows, but is " + row);
		// 10 - col because width starts with 1 and not 0
		if (width < 1 || width > 10 - col)
			throw new IllegalArgumentException("The width must be between 1 and " + (10 - col) + ", but is " + width);
		if (height < 1 || height > getRows() + 1 - row)
			throw new IllegalArgumentException(
					"The height must be between 1 and " + (getRows() + 1 - row) + ", but is " + height);
		fillRectangle(locToPos(col, row), width, height, item);
	}

	public void fillRectangle(int pos, int width, int height, ClickableItem item) {
		if (pos < 0 || pos > size * 9)
			throw new IllegalArgumentException("pos must be between 0 and " + (size * 9) + ", but is " + pos);
		int[] colRow = posToLoc(pos);
		int row = colRow[0];
		int col = colRow[1];

		if (col < 1 || col > 9)
			throw new IllegalArgumentException("col must be between 1 and 9, but is " + col);
		if (row < 1 || row > 6)
			throw new IllegalArgumentException("row must be between 1 and the maximum number of rows, but is " + row);
		// 10 - col because width starts with 1 and not 0
		if (width < 1 || width > 10 - col)
			throw new IllegalArgumentException("The width must be between 1 and " + (10 - col) + ", but is " + width);
		if (height < 1 || height > size + 1 - row)
			throw new IllegalArgumentException(
					"The height must be between 1 and " + (size + 1 - row) + ", but is " + height);
		for (int i = col; i < col + width; i++)
			for (int j = row; j < row + height; j++)
				set(i, j, item);
	}

	public void open() {
		player.openInventory(bukkitInventory);
	}

	public void handler(InventoryClickEvent e) {
		int pos = e.getSlot();
		if (pos < 0 || pos > items.length)
			return;
		ClickableItem item = items[pos];
		// Nothing to do
		if (item == null)
			return;
		item.run(e);
	}

	public void put(String key, Object value) {
		values.put(key, value);
	}

	public Object get(String key) {
		return values.get(key);
	}

	public int[] posToLoc(int pos) {
		return new int[] { (pos / 9) + 1, (pos % 9) + 1 };
	}

	public int locToPos(int col, int row) {
		return (row - 1) * 9 + (col - 1);
	}
}
