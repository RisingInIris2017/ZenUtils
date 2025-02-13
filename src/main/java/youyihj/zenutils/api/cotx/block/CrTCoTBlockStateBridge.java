package youyihj.zenutils.api.cotx.block;

import com.teamacronymcoders.contenttweaker.api.ctobjects.blockstate.ICTBlockState;
import crafttweaker.api.block.IBlockState;
import crafttweaker.mc1120.block.MCBlockState;
import stanhebben.zenscript.annotations.ZenCaster;
import stanhebben.zenscript.annotations.ZenExpansion;

/**
 * @author youyihj
 */
@ZenExpansion("mods.contenttweaker.BlockState")
public class CrTCoTBlockStateBridge {
    @ZenCaster
    public static IBlockState castToCrT(ICTBlockState state) {
        return new MCBlockState(state.getInternal());
    }
}
