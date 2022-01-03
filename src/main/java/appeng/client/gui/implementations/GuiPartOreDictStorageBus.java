package appeng.client.gui.implementations;

import appeng.client.gui.AEBaseGui;
import appeng.client.gui.widgets.GuiTabButton;
import appeng.client.gui.widgets.MEGuiTextField;
import appeng.container.implementations.ContainerPartOreDictStorageBus;
import appeng.core.localization.GuiText;
import appeng.core.sync.GuiBridge;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.PacketSwitchGuis;
import appeng.core.sync.packets.PacketValueConfig;
import appeng.parts.misc.PartOreDicStorageBus;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;

import java.io.IOException;
import java.util.regex.Pattern;


public class GuiPartOreDictStorageBus extends AEBaseGui
{
    PartOreDicStorageBus part;
    private GuiTabButton priority;

    private static final Pattern ORE_DICTIONARY_FILTER = Pattern.compile( "\\*?[a-zA-Z0-9_|\\\\+(){,}?!=\\-\\[:\\]&^$]*\\*?" );
    private MEGuiTextField searchFieldInputs;

    public GuiPartOreDictStorageBus( final InventoryPlayer inventoryPlayer, final PartOreDicStorageBus te )
    {
        super( new ContainerPartOreDictStorageBus( inventoryPlayer, te ) );
        part = te;
        this.ySize = 84;
    }

    @Override
    public void initGui()
    {
        super.initGui();

        this.searchFieldInputs = new MEGuiTextField( this.fontRenderer, this.guiLeft + 3, this.guiTop + 22, 170, 12 );
        this.searchFieldInputs.setEnableBackgroundDrawing( false );
        this.searchFieldInputs.setMaxStringLength( 512 );
        this.searchFieldInputs.setTextColor( 0xFFFFFF );
        this.searchFieldInputs.setVisible( true );
        this.searchFieldInputs.setFocused( false );
        this.searchFieldInputs.setValidator( str -> ORE_DICTIONARY_FILTER.matcher( str ).matches() );

        this.buttonList.add( this.priority = new GuiTabButton( this.guiLeft + 154, this.guiTop, 2 + 4 * 16, GuiText.Priority.getLocal(), this.itemRender ) );

        try
        {
            NetworkHandler.instance().sendToServer( new PacketValueConfig( "OreDictStorageBus.getRegex", "1" ) );
        }
        catch( IOException e )
        {
            e.printStackTrace();
        }

    }

    public void fillRegex( String regex )
    {
        this.searchFieldInputs.setText( regex );
    }

    @Override
    protected void actionPerformed( final GuiButton btn ) throws IOException
    {
        super.actionPerformed( btn );
        if( btn == this.priority )
        {
            NetworkHandler.instance().sendToServer( new PacketSwitchGuis( GuiBridge.GUI_PRIORITY ) );
        }
    }

    @Override
    protected void mouseClicked( final int xCoord, final int yCoord, final int btn ) throws IOException
    {
        boolean wasFocused = this.searchFieldInputs.isFocused();
        this.searchFieldInputs.mouseClicked( xCoord, yCoord, btn );

        if( btn == 1 && this.searchFieldInputs.isMouseIn( xCoord, yCoord ) )
        {
            this.searchFieldInputs.setText( "" );
        }

        if( !searchFieldInputs.isFocused() && wasFocused )
        {
            NetworkHandler.instance().sendToServer( new PacketValueConfig( "OreDictStorageBus.save", searchFieldInputs.getText() ) );
        }

        super.mouseClicked( xCoord, yCoord, btn );
    }

    @Override
    protected void keyTyped( final char character, final int key ) throws IOException
    {
        if( !this.checkHotbarKeys( key ) )
        {
            if( !this.searchFieldInputs.textboxKeyTyped( character, key ) )
            {
                super.keyTyped( character, key );
            }
        }
    }

    @Override
    public void drawFG( int offsetX, int offsetY, int mouseX, int mouseY )
    {
        this.fontRenderer.drawString( this.getGuiDisplayName( GuiText.OreDictStorageBus.getLocal() ), 8, 6, 4210752 );
        this.fontRenderer.drawString( this.searchFieldInputs.getText().length() + " / " + this.searchFieldInputs.getMaxStringLength(), 8, 36, 4210752 );
        this.fontRenderer.drawSplitString( "Supports regex. Or use * as a wildcard", 6, 48, 174, 4210752 );
    }

    @Override
    public void drawBG( int offsetX, int offsetY, int mouseX, int mouseY )
    {
        this.bindTexture( "guis/oredictstoragebus.png" );
        this.drawTexturedModalRect( offsetX, offsetY, 0, 0, 175, 85 );

        if( this.searchFieldInputs != null )
        {
            this.searchFieldInputs.drawTextBox();
        }
    }

}