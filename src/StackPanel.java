import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.util.List;

import javax.swing.JPanel;

public class StackPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private final List<StackEntry> stack;
	private final StackPanelProperties p;
	private final int entrywidth;
	private final int stackwidth;

	public StackPanel(final List<StackEntry> stack, final StackPanelProperties p) {
		this( stack, p, new BorderLayout());
	}

	public StackPanel(final List<StackEntry> stack,
			final StackPanelProperties p, LayoutManager layout) {
		super(layout);
		this.stack = stack;
		this.p = p;
		entrywidth = p.insidePad * 4 + p.actionWidth + p.stateWidth
				+ p.nActions * p.actionWidth + (p.nActions - 1) * p.actionsPad;
		stackwidth = p.outsidePad * 2 + entrywidth;
		this.setMinimumSize(new Dimension(stackwidth+10,100));
		this.setPreferredSize(this.getMinimumSize());		
	}

	private void drawLabel(Graphics g, String str, Rectangle geo, Color line,
			Color background, Color fore) {
		FontMetrics metrics = g.getFontMetrics(g.getFont());
		g.setColor(background);
		g.fillRect(geo.x, geo.y, geo.width, geo.height);
		g.setColor(line);
		g.drawRect(geo.x, geo.y, geo.width, geo.height);
		g.drawString(str, geo.x + (geo.width / 2)
				- (metrics.stringWidth(str) / 2), geo.y + geo.height - 2);
	}

	private void drawStackBackground(Graphics g) {
		FontMetrics metrics = g.getFontMetrics(g.getFont());
		int stateHeight = metrics.getHeight();
		int entryheight = stateHeight + p.insidePad * 2;

		// GET STACK HEIGHT
		int entries = 2;
		if (stack.size() > 0) {
			entries = (int) Math.floor(Math.log(stack.size()) / Math.log(2));
			entries = (int) Math.pow(2, entries + 1);
		}

		int stackheight = entryheight * entries + p.outsidePad * entries;

		// DRAW STACK BACKGROUND
		int stackx = (this.getWidth() - stackwidth) / 2;
		int stacky = this.getHeight() - stackheight - 10;
		int LINE_WIDTH = 3;
		g.setColor(Color.WHITE);
		g.fillRect(stackx, stacky, stackwidth, stackheight);
		g.setColor(Color.BLACK);
		g.fillRect(stackx, stacky, LINE_WIDTH, stackheight);
		g.fillRect(stackx + stackwidth - LINE_WIDTH, stacky, LINE_WIDTH,
				stackheight);
		g.fillRect(stackx, stacky + stackheight - LINE_WIDTH, stackwidth,
				LINE_WIDTH);

		// DRAW ENTRIES
		int entryx, entryy;
		int actionx, actiony;
		int actionwidth, actionheight;
		int statex, statey;
		int statewidth, stateheight;
		int posx, posy;
		int poswidth, posheight;
		for (int i = 0; i < stack.size(); i++) {
			
			// ENTRY
			StackEntry entry = stack.get(i);
			entryx = stackx + p.insidePad;
			entryy = stacky + stackheight
					- ((entryheight + p.outsidePad) * (i + 1));
			drawLabel(g, "", new Rectangle(entryx, entryy, entrywidth,
					entryheight), Color.BLACK, Color.GRAY, Color.BLACK);

			// ACTION
			String action = entry.getAction();
			actionx = entryx + p.insidePad;
			actiony = entryy + p.insidePad;
			actionwidth = p.actionWidth;
			actionheight = stateHeight;
			drawLabel(g, action, new Rectangle(actionx, actiony, actionwidth,
					actionheight), Color.BLACK, Color.WHITE, Color.BLACK);

			// STATE
			String state = entry.getState();
			statex = actionx + actionwidth + p.insidePad;
			statey = entryy + p.insidePad;
			statewidth = p.stateWidth;
			stateheight = stateHeight;
			drawLabel(g, state, new Rectangle(statex, statey, statewidth,
					stateheight), Color.BLACK, Color.BLUE, Color.BLACK);
			
			// ACTIONS
			posx = statex + statewidth + p.insidePad;
			posy = entryy + p.insidePad;
			posheight = stateHeight;
			poswidth = p.actionWidth;
			for( String posAction : entry.getActions()) {
				drawLabel(g, posAction, new Rectangle(posx, posy, poswidth,
						posheight), Color.BLACK, Color.GREEN, Color.BLACK);
				posx += poswidth+p.actionsPad;
			}
		}
		this.setPreferredSize(new Dimension(stackwidth+10, stackheight));
	}

	@Override
	public void paintComponent(Graphics g) {
		// ERASE
		super.paintComponent(g);
		drawStackBackground(g);
	}

}
