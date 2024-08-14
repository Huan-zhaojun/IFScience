package com.ddhuan.ifscience.common.Entity.render;

import com.ddhuan.ifscience.common.Entity.MagnetAttractedBlockEntity;
import com.ddhuan.ifscience.common.Entity.entityTypeRegistry;
import net.minecraft.client.renderer.entity.TNTRenderer;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class EntityRenderRegistryManager {
    private EntityRenderRegistryManager() {
    }

    public static void register() {
        //注册实体渲染器
        RenderingRegistry.registerEntityRenderingHandler(entityTypeRegistry.furnaceTNT.get(), furnaceTNTRender::new);
        RenderingRegistry.registerEntityRenderingHandler(entityTypeRegistry.touchdownTNT.get(), TNTRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(entityTypeRegistry.blockEntity.get(), blockEntityRender::new);
        RenderingRegistry.registerEntityRenderingHandler(entityTypeRegistry.MagnetAttractedBlockEntity.get(), blockEntityRender<MagnetAttractedBlockEntity>::new);
    }
}
