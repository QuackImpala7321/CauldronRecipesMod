package net.quackimpala7321.crm.mixin;

import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.quackimpala7321.crm.CauldronRecipe;
import net.quackimpala7321.crm.CauldronRecipesMod;
import net.quackimpala7321.crm.util.ItemStackUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

@Mixin(AbstractCauldronBlock.class)
public class AbstractCauldronMixin {
    @Unique
    private final AbstractCauldronBlock thisBlock = (AbstractCauldronBlock) (Object) this;
    @Unique
    private static final Predicate<ItemEntity> ANY = itemEntity -> true;

    @Inject(method = "onUse", at = @At("HEAD"))
    public void onUseMixin(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if(world.isClient
                || !(thisBlock instanceof LeveledCauldronBlock)
                || world.getBlockState(pos).get(Properties.LEVEL_3) < 1) return;

        List<ItemEntity> tmpItemEntities = world.getEntitiesByClass(ItemEntity.class, new Box(pos),
                ANY);

        // Filters duplicates
        List<ItemEntity> itemEntities = new ArrayList<>();
        for (ItemEntity tmpItemEntity : tmpItemEntities) {
            boolean has = false;
            Item tmpItem = tmpItemEntity.getStack().getItem();

            for (ItemEntity itemEntity : itemEntities) {
                Item item = itemEntity.getStack().getItem();

                if(tmpItem == item) {
                    has = true;
                    break;
                }
            }

            if(!has) {
                itemEntities.add(tmpItemEntity);
            }
        }

        List<ItemStack> input = new ArrayList<>();
        itemEntities.forEach(itemEntity -> input.add(itemEntity.getStack()));
        input.sort(Comparator.comparing(ItemStack::getTranslationKey));
        List<CauldronRecipe> matchingRecipes = CauldronRecipesMod.CAULDRON_RECIPE_MANAGER.getRecipes().values().stream().filter(recipe -> ItemStackUtil.equalsList(recipe.getInput(), input)).toList();
        if(matchingRecipes.isEmpty()) return;
        CauldronRecipe recipe = matchingRecipes.get(0);

        double xVelocity = 0.15 - (world.random.nextDouble() * 0.3);
        double zVelocity = 0.15 - (world.random.nextDouble() * 0.3);

        ServerWorld serverWorld = (ServerWorld) world;
        ItemEntity outputEntity = new ItemEntity(world,
                pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
                recipe.getOutput());
        outputEntity.setVelocity(xVelocity, 0.5, zVelocity);

        itemEntities.forEach(Entity::discard);
        LeveledCauldronBlock.decrementFluidLevel(state, world, pos);

        serverWorld.spawnEntity(outputEntity);
        player.addExperience(recipe.getExperience());
        this.spawnSuccessParticles(serverWorld, pos);
    }

    @Unique
    private void spawnSuccessParticles(ServerWorld serverWorld, BlockPos pos) {
        for (int i = 0; i < 10; i++) {
            double xOffset = Math.sin(i * 36) / 2;
            double yOffset = (double) i / 10;
            double zOffset = Math.tan(i * 36) / 2;

            serverWorld.spawnParticles(ParticleTypes.COMPOSTER,
                    pos.getX() + 0.5 + xOffset, pos.getY() + 1.5 + yOffset, pos.getZ() + 0.5 + zOffset, 1,
                    0, 0, 0 ,0);
        }
    }
}
