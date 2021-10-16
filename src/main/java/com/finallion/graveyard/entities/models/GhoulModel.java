package com.finallion.graveyard.entities.models;

import com.finallion.graveyard.entities.GhoulEntity;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.util.math.MatrixStack;


public class GhoulModel extends BipedEntityModel<GhoulEntity> {
	private final ModelPart root;
	/*
	private final ModelPart lower_leg_right;
	private final ModelPart lower_leg_left;
	private final ModelPart left_foot;
	private final ModelPart right_foot;
	private final ModelPart upper_body;
	private final ModelPart jaw;
	private final ModelPart right_lower_arm;
	private final ModelPart left_lower_arm;

	 */
	private final ModelPart left_arm;
	private final ModelPart right_arm;
	private final ModelPart head;
	private final ModelPart leg_right;
	private final ModelPart leg_left;
	private final ModelPart body;


	public GhoulModel(ModelPart root) {
		super(root);
		this.root = root;
		this.leg_right = root.getChild(EntityModelPartNames.RIGHT_LEG);
		this.leg_left = root.getChild(EntityModelPartNames.LEFT_LEG);
		this.body = root.getChild(EntityModelPartNames.BODY);
		this.head = root.getChild(EntityModelPartNames.HEAD);
		this.left_arm = root.getChild(EntityModelPartNames.LEFT_ARM);
		this.right_arm = root.getChild(EntityModelPartNames.RIGHT_ARM);
		/*
		this.right_foot = root.getChild("right_foot");
		this.left_foot = root.getChild("left_foot");
		this.lower_leg_right = root.getChild("lower_leg_right");
		this.lower_leg_left = root.getChild("lower_leg_left");
		this.upper_body = root.getChild("upper_body");
		this.jaw = root.getChild("jaw");
		this.left_lower_arm = root.getChild("left_lower_arm");
		this.right_lower_arm = root.getChild("right_lower_arm");

		 */




	}

	// y = up - down -> - = up; + = down
	// x = east - west -> - = west; + = east
	// z = north - south

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();

		ModelPartData hat = modelPartData.addChild(EntityModelPartNames.HAT, ModelPartBuilder.create(), ModelTransform.NONE);

		ModelPartData leg_left = modelPartData.addChild(EntityModelPartNames.LEFT_LEG, ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
		ModelPartData leg_right = modelPartData.addChild(EntityModelPartNames.RIGHT_LEG, ModelPartBuilder.create(), ModelTransform.NONE);
		leg_left.addChild("upper_leg_left", ModelPartBuilder.create().uv(16, 31).cuboid(-4.0F, -12.0F, 5.0F, 3.0F, 8.0F, 3.0F), ModelTransform.of(0.0F, 26.0F, 0.0F, -2.3998F, 0.2182F, -3.1416F));
		leg_right.addChild("upper_leg_right", ModelPartBuilder.create().uv(44, 48).cuboid(-4.0F, -13.0F, -7.0F, 3.0F, 8.0F, 3.0F), ModelTransform.of(0.0F, 26.5F, 0.0F, -0.6345F, 0.2182F, 0.0F));
		leg_left.addChild("left_foot", ModelPartBuilder.create().uv(0, 49).cuboid(-2.0F, 0.0F, -1.0F, 4.0F, 1.0F, 4.0F), ModelTransform.pivot(3.0F, 25.0F, 0.0F));
		leg_right.addChild("right_foot", ModelPartBuilder.create().uv(46, 0).cuboid(-2.0F, 0.0F, -1.0F, 4.0F, 1.0F, 4.0F), ModelTransform.pivot(-3.0F, 25.0F, -1.0F));
		leg_right.addChild("lower_leg_right", ModelPartBuilder.create().uv(16, 42).cuboid(1.0F, -7.0F, -5.0F, 4.0F, 8.0F, 4.0F), ModelTransform.of(0.0F, 26.0F, 0.0F, -0.7854F, 3.1416F, 0.0F));
		leg_left.addChild("lower_leg_left", ModelPartBuilder.create().uv(44, 26).cuboid(-5.0F, -7.0F, -5.0F, 4.0F, 8.0F, 4.0F), ModelTransform.of(0.0F, 26.0F, 0.0F, -0.7854F, 3.1416F, 0.0F));

		ModelPartData body = modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create(), ModelTransform.rotation(0.0F, 0.0F, 0.0F));
		body.addChild("rib_1", ModelPartBuilder.create().uv(0, 0).cuboid(-3.0F, -17.0F, 8.0F, 2.0F, 2.0F, 2.0F), ModelTransform.of(0.0F, 24.0F, 0.0F, 0.3491F, 0.0F, 0.0F));
		body.addChild("rib_2", ModelPartBuilder.create().uv(0, 4).cuboid(1.0F, -17.0F, 8.0F, 2.0F, 2.0F, 2.0F), ModelTransform.of(0.0F, 24.0F, 0.0F, 0.3491F, 0.0F, 0.0F));
		body.addChild("rib_3", ModelPartBuilder.create().uv(0, 13).cuboid(-4.0F, -20.0F, 5.0F, 0.0F, 2.0F, 3.0F), ModelTransform.of(0.0F, 24.0F, 0.0F, 0.3491F, 0.0F, 0.0F));
		body.addChild("rib_4", ModelPartBuilder.create().uv(0, 29).cuboid(-3.0F, -17.0F, 6.0F, 0.0F, 2.0F, 2.0F), ModelTransform.of(0.0F, 24.0F, 0.0F, 0.3491F, 0.0F, 0.0F));
		body.addChild("rib_5", ModelPartBuilder.create().uv(12, 29).cuboid(3.0F, -17.0F, 6.0F, 0.0F, 2.0F, 2.0F), ModelTransform.of(0.0F, 24.0F, 0.0F, 0.3491F, 0.0F, 0.0F));
		body.addChild("rib_6", ModelPartBuilder.create().uv(0, 15).cuboid(4.0F, -20.0F, 5.0F, 0.0F, 2.0F, 3.0F), ModelTransform.of(0.0F, 24.0F, 0.0F, 0.3491F, 0.0F, 0.0F));
		body.addChild("rib_7", ModelPartBuilder.create().uv(56, 24).cuboid(1.0F, -20.0F, 8.0F, 3.0F, 2.0F, 2.0F), ModelTransform.of(0.0F, 24.0F, 0.0F, 0.3491F, 0.0F, 0.0F));
		body.addChild("rib_8", ModelPartBuilder.create().uv(56, 50).cuboid(-4.0F, -20.0F, 8.0F, 3.0F, 2.0F, 2.0F), ModelTransform.of(0.0F, 24.0F, 0.0F, 0.3491F, 0.0F, 0.0F));
		body.addChild("rib_9", ModelPartBuilder.create().uv(53, 48).cuboid(-4.0F, -20.0F, 5.0F, 8.0F, 2.0F, 0.0F), ModelTransform.of(0.0F, 24.0F, 0.0F, 0.3491F, 0.0F, 0.0F));
		body.addChild("rib_10", ModelPartBuilder.create().uv(8, 54).cuboid(-1.0F, -21.0F, 8.0F, 2.0F, 8.0F, 2.0F), ModelTransform.of(0.0F, 24.0F, 0.0F, 0.3491F, 0.0F, 0.0F));
		body.addChild("hip", ModelPartBuilder.create().uv(40, 42).cuboid(-4.0F, -14.0F, 5.0F, 8.0F, 2.0F, 4.0F), ModelTransform.of(0.0F, 24.0F, 0.0F, 0.2618F, 0.0F, 0.0F));


		ModelPartData head = modelPartData.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create(), ModelTransform.rotation(0.0F, 0.0F, 0.0F));
		head.addChild("head_part_1", ModelPartBuilder.create().uv(24, 8).cuboid(-6.0F, -32.0F, 12.0F, 2.0F, 5.0F, 8.0F), ModelTransform.of(0.0F, 24.0F, 0.0F, -3.0543F, 0.0F, -3.1416F));
		head.addChild("head_part_2", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -33.0F, 12.0F, 8.0F, 8.0F, 8.0F), ModelTransform.of(0.0F, 24.0F, 0.0F, -3.0543F, 0.0F, -3.1416F));
		head.addChild("head_part_3", ModelPartBuilder.create().uv(36, 13).cuboid(4.0F, -32.0F, 12.0F, 2.0F, 5.0F, 8.0F), ModelTransform.of(0.0F, 24.0F, 0.0F, -3.0543F, 0.0F, -3.1416F));

		ModelPartData upper_body = body.addChild("upper_body", ModelPartBuilder.create(), ModelTransform.rotation(0.0F, 0.0F, -0.0F));
		upper_body.addChild("rib_1", ModelPartBuilder.create().uv(58, 0).cuboid(1.0F, -19.0F, 15.0F, 3.0F, 2.0F, 2.0F), ModelTransform.of(0.0F, 24.0F, 0.0F, 0.6981F, 0.0F, 0.0F));
		upper_body.addChild("rib_2", ModelPartBuilder.create().uv(56, 54).cuboid(-4.0F, -19.0F, 15.0F, 3.0F, 2.0F, 2.0F), ModelTransform.of(0.0F, 24.0F, 0.0F, 0.6981F, 0.0F, 0.0F));
		upper_body.addChild("rib_3", ModelPartBuilder.create().uv(52, 5).cuboid(-4.0F, -20.0F, 10.0F, 8.0F, 3.0F, 0.0F), ModelTransform.of(0.0F, 24.0F, 0.0F, 0.6981F, 0.0F, 0.0F));
		upper_body.addChild("rib_4", ModelPartBuilder.create().uv(0, 22).cuboid(-5.0F, -22.0F, 10.0F, 3.0F, 2.0F, 0.0F), ModelTransform.of(0.0F, 24.0F, 0.0F, 0.6981F, 0.0F, 0.0F));
		upper_body.addChild("rib_5", ModelPartBuilder.create().uv(0, 20).cuboid(2.0F, -22.0F, 10.0F, 3.0F, 2.0F, 0.0F), ModelTransform.of(0.0F, 24.0F, 0.0F, 0.6981F, 0.0F, 0.0F));
		upper_body.addChild("rib_6", ModelPartBuilder.create().uv(56, 20).cuboid(-5.0F, -22.0F, 15.0F, 4.0F, 2.0F, 2.0F), ModelTransform.of(0.0F, 24.0F, 0.0F, 0.6981F, 0.0F, 0.0F));
		upper_body.addChild("rib_7", ModelPartBuilder.create().uv(16, 56).cuboid(1.0F, -22.0F, 15.0F, 4.0F, 2.0F, 2.0F), ModelTransform.of(0.0F, 24.0F, 0.0F, 0.6981F, 0.0F, 0.0F));
		upper_body.addChild("rib_8", ModelPartBuilder.create().uv(14, 49).cuboid(5.0F, -22.0F, 10.0F, 0.0F, 2.0F, 5.0F), ModelTransform.of(0.0F, 24.0F, 0.0F, 0.6981F, 0.0F, 0.0F));
		upper_body.addChild("rib_9", ModelPartBuilder.create().uv(36, 5).cuboid(4.0F, -19.0F, 10.0F, 0.0F, 2.0F, 5.0F), ModelTransform.of(0.0F, 24.0F, 0.0F, 0.6981F, 0.0F, 0.0F));
		upper_body.addChild("rib_10", ModelPartBuilder.create().uv(36, 3).cuboid(-4.0F, -19.0F, 10.0F, 0.0F, 2.0F, 5.0F), ModelTransform.of(0.0F, 24.0F, 0.0F, 0.6981F, 0.0F, 0.0F));
		upper_body.addChild("rib_11", ModelPartBuilder.create().uv(24, 16).cuboid(-5.0F, -22.0F, 10.0F, 0.0F, 2.0F, 5.0F), ModelTransform.of(0.0F, 24.0F, 0.0F, 0.6981F, 0.0F, 0.0F));
		upper_body.addChild("rib_12", ModelPartBuilder.create().uv(0, 54).cuboid(-1.0F, -26.0F, 15.0F, 2.0F, 10.0F, 2.0F), ModelTransform.of(0.0F, 24.0F, 0.0F, 0.6981F, 0.0F, 0.0F));
		upper_body.addChild("upper_body", ModelPartBuilder.create().uv(0, 16).cuboid(-4.0F, -31.0F, 2.0F, 8.0F, 7.0F, 8.0F), ModelTransform.of(0.0F, 24.0F, 0.0F, 3.1416F, 0.0F, -3.1416F));



		ModelPartData jaw = head.addChild("jaw", ModelPartBuilder.create(), ModelTransform.rotation(0.0F, 0.0F, 0.0F));
		jaw.addChild("side_teeth_1", ModelPartBuilder.create().uv(32, 23).cuboid(3.0F, -31.0F, -5.0F, 1.0F, 3.0F, 1.0F), ModelTransform.of(0.0F, 24.0F, 0.0F, 0.4363F, 0.0F, 0.0F));
		jaw.addChild("side_teeth_2", ModelPartBuilder.create().uv(0, 33).cuboid(-4.0F, -29.0F, -4.0F, 1.0F, 1.0F, 1.0F), ModelTransform.of(0.0F, 24.0F, 0.0F, 0.4363F, 0.0F, 0.0F));
		jaw.addChild("side_teeth_3", ModelPartBuilder.create().uv(46, 0).cuboid(-4.0F, -30.0F, -5.0F, 1.0F, 2.0F, 1.0F), ModelTransform.of(0.0F, 24.0F, 0.0F, 0.4363F, 0.0F, 0.0F));
		jaw.addChild("side_teeth_4", ModelPartBuilder.create().uv(16, 42).cuboid(3.0F, -29.0F, -9.0F, 1.0F, 2.0F, 1.0F), ModelTransform.of(0.0F, 24.0F, 0.0F, 0.2618F, 0.0F, 0.0F));
		jaw.addChild("side_teeth_5", ModelPartBuilder.create().uv(15, 32).cuboid(3.0F, -28.0F, -8.0F, 1.0F, 1.0F, 1.0F), ModelTransform.of(0.0F, 24.0F, 0.0F, 0.2618F, 0.0F, 0.0F));
		jaw.addChild("side_teeth_6", ModelPartBuilder.create().uv(44, 26).cuboid(-4.0F, -28.0F, -9.0F, 1.0F, 2.0F, 1.0F), ModelTransform.of(0.0F, 24.0F, 0.0F, 0.2182F, 0.0F, 0.0F));
		jaw.addChild("teeth_1", ModelPartBuilder.create().uv(36, 12).cuboid(-3.0F, -33.0F, -1.0F, 1.0F, 3.0F, 1.0F), ModelTransform.of(0.0F, 24.0F, 0.0F, 0.6109F, 0.0F, 0.0F));
		jaw.addChild("teeth_2", ModelPartBuilder.create().uv(40, 26).cuboid(-1.0F, -34.0F, -1.0F, 1.0F, 4.0F, 1.0F), ModelTransform.of(0.0F, 24.0F, 0.0F, 0.6109F, 0.0F, 0.0F));
		jaw.addChild("teeth_3", ModelPartBuilder.create().uv(24, 0).cuboid(2.0F, -34.0F, -1.0F, 1.0F, 4.0F, 1.0F), ModelTransform.of(0.0F, 24.0F, 0.0F, 0.6109F, 0.0F, 0.0F));
		jaw.addChild("teeth_4", ModelPartBuilder.create().uv(46, 8).cuboid(-2.0F, -32.0F, -1.0F, 1.0F, 2.0F, 1.0F), ModelTransform.of(0.0F, 24.0F, 0.0F, 0.6109F, 0.0F, 0.0F));
		jaw.addChild("teeth_5", ModelPartBuilder.create().uv(12, 33).cuboid(-4.0F, -31.0F, -1.0F, 1.0F, 1.0F, 1.0F), ModelTransform.of(0.0F, 24.0F, 0.0F, 0.6109F, 0.0F, 0.0F));
		jaw.addChild("teeth_6", ModelPartBuilder.create().uv(32, 46).cuboid(0.0F, -31.0F, -1.0F, 1.0F, 1.0F, 1.0F), ModelTransform.of(0.0F, 24.0F, 0.0F, 0.6109F, 0.0F, 0.0F));
		jaw.addChild("teeth_7", ModelPartBuilder.create().uv(41, 48).cuboid(1.0F, -32.0F, -1.0F, 1.0F, 2.0F, 1.0F), ModelTransform.of(0.0F, 24.0F, 0.0F, 0.6109F, 0.0F, 0.0F));
		jaw.addChild("teeth_8", ModelPartBuilder.create().uv(40, 12).cuboid(3.0F, -33.0F, -1.0F, 1.0F, 3.0F, 1.0F), ModelTransform.of(0.0F, 24.0F, 0.0F, 0.6109F, 0.0F, 0.0F));
		jaw.addChild("jaw", ModelPartBuilder.create().uv(24, 0).cuboid(-4.0F, -27.0F, 3.0F, 8.0F, 2.0F, 6.0F), ModelTransform.of(0.0F, 24.0F, 0.0F, 2.8798F, 0.0F, -3.1416F));
		jaw.addChild("jaw_front", ModelPartBuilder.create().uv(44, 38).cuboid(-4.0F, -30.0F, -3.0F, 8.0F, 3.0F, 1.0F), ModelTransform.of(0.0F, 24.0F, 0.0F, 2.4435F, 0.0F, -3.1416F));


		ModelPartData left_arm = modelPartData.addChild(EntityModelPartNames.LEFT_ARM, ModelPartBuilder.create(), ModelTransform.NONE);
		ModelPartData right_arm = modelPartData.addChild(EntityModelPartNames.RIGHT_ARM, ModelPartBuilder.create(), ModelTransform.NONE);
		left_arm.addChild("left_upper_arm", ModelPartBuilder.create().uv(32, 48).cuboid(-22.0F, -21.0F, 9.0F, 3.0F, 9.0F, 3.0F), ModelTransform.of(0.0F, 25.0F, 0.0F, -2.8798F, 0.0F, 2.5744F));
		right_arm.addChild("right_upper_arm", ModelPartBuilder.create().uv(48, 8).cuboid(18.0F, -22.0F, 10.0F, 3.0F, 9.0F, 3.0F), ModelTransform.of(0.0F, 24.0F, 0.0F, -2.8798F, 0.0F, -2.5744F));
		right_arm.addChild("right_lower_arm", ModelPartBuilder.create().uv(0, 31).cuboid(5.0F, -21.0F, 11.0F, 4.0F, 14.0F, 4.0F), ModelTransform.of(0.0F, 24.0F, 0.0F, -2.8798F, 0.0F, 3.0543F));
		left_arm.addChild("left_lower_arm", ModelPartBuilder.create().uv(28, 27).cuboid(-9.0F, -20.0F, 12.0F, 4.0F, 11.0F, 4.0F), ModelTransform.of(0.0F, 25.0F, 0.0F, -2.8362F, 0.0F, -3.0543F));




		return TexturedModelData.of(modelData, 128, 128);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
		ImmutableList.of(this.root).forEach((modelRenderer) -> {
			modelRenderer.render(matrices, vertices, light, overlay, red, green, blue, alpha);
		});
	}


	@Override
	public void setAngles(GhoulEntity livingEntity, float f, float g, float h, float i, float j) {
		super.setAngles(livingEntity, f, g, h, i, j);


		// sets parts in right position
		this.leg_left.pivotY = -2.5F;
		this.leg_right.pivotY = -2.5F;
		this.leg_right.getChild("upper_leg_right").pivotZ = -0.5F;

		this.left_arm.pivotZ = -2.0F;
		this.left_arm.pivotX = -0.55F;
		this.left_arm.pivotY = 0.1F;

		this.right_arm.pivotX = -0.8F;
		this.right_arm.pivotZ = -2.0F;

	}

	/*
	ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData right_foot = modelPartData.addChild(EntityModelPartNames.RIGHT_FRONT_FOOT, ModelPartBuilder.create().uv(0, 49).cuboid(-2.0F, 0.0F, -1.0F, 4.0F, 1.0F, 4.0F), ModelTransform.pivot(3.0F, 23.0F, -1.0F));

		ModelPartData left_foot = modelPartData.addChild(EntityModelPartNames.LEFT_FRONT_FOOT, ModelPartBuilder.create().uv(46, 0).cuboid(-2.0F, 0.0F, -1.0F, 4.0F, 1.0F, 4.0F), ModelTransform.pivot(-3.0F, 23.0F, -1.0F));

		//ModelPartData lower_leg_right_model = modelPartData.addChild("lower_leg_right", ModelPartBuilder.create(), ModelTransform.rotation(3.0F, 17.0F, 1.0F));
		ModelPartData lower_leg_right_model = modelPartData.addChild("lower_leg_right", ModelPartBuilder.create().uv(16, 42).cuboid(1.0F, -7.0F, -5.0F, 4.0F, 8.0F, 4.0F), ModelTransform.of(0.0F, 24.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

		//ModelPartData lower_leg_left_model = modelPartData.addChild("lower_leg_left", ModelPartBuilder.create(), ModelTransform.rotation(-3.0F, 17.0F, 1.0F));
		ModelPartData lower_leg_left_model = modelPartData.addChild("lower_leg_left", ModelPartBuilder.create().uv(44, 26).cuboid(-5.0F, -7.0F, -5.0F, 4.0F, 8.0F, 4.0F), ModelTransform.of(0.0F, 24.0F, 0.0F,  -0.7854F, 0.0F, 0.0F));


		ModelPartData upper_leg_right = modelPartData.addChild(EntityModelPartNames.RIGHT_LEG, ModelPartBuilder.create(), ModelTransform.rotation(2.0F, 11.0F, -2.0F));
		upper_leg_right.addChild("upper_leg_right_r1", ModelPartBuilder.create().uv(16, 31).cuboid(1.0F, -13.0F, 4.0F, 3.0F, 8.0F, 3.0F), ModelTransform.of(0.0F, 13.0F, 2.0F, 0.6545F, 0.2182F, 0.0F));

		ModelPartData upper_leg_left = modelPartData.addChild(EntityModelPartNames.LEFT_LEG, ModelPartBuilder.create(), ModelTransform.rotation(-2.0F, 11.0F, -2.0F));
		upper_leg_left.addChild("upper_leg_left_r1", ModelPartBuilder.create().uv(44, 48).cuboid(-4.0F, -13.0F, 4.0F, 3.0F, 8.0F, 3.0F), ModelTransform.of(2.0F, 13.0F, 2.0F, 0.6545F, -0.2182F, 0.0F));

		ModelPartData body = modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create(), ModelTransform.rotation(0.0F, 9.8182F, -3.6364F));
		body.addChild("rib_1", ModelPartBuilder.create().uv(0, 0).cuboid(-3.0F, -17.0F, -10.0F, 2.0F, 2.0F, 2.0F), ModelTransform.of(0.0F, 14.1818F, 3.6364F, -0.3491F, 0.0F, 0.0F));
		body.addChild("rib_2", ModelPartBuilder.create().uv(0, 4).cuboid(1.0F, -17.0F, -10.0F, 2.0F, 2.0F, 2.0F), ModelTransform.of(0.0F, 14.1818F, 3.6364F, -0.3491F, 0.0F, 0.0F));
		body.addChild("rib_3", ModelPartBuilder.create().uv(0, 13).cuboid(-4.0F, -20.0F, -8.0F, 0.0F, 2.0F, 3.0F), ModelTransform.of(0.0F, 14.1818F, 3.6364F, -0.3491F, 0.0F, 0.0F));
		body.addChild("rib_4", ModelPartBuilder.create().uv(0, 29).cuboid(-3.0F, -17.0F, -8.0F, 0.0F, 2.0F, 2.0F), ModelTransform.of(0.0F, 14.1818F, 3.6364F, -0.3491F, 0.0F, 0.0F));
		body.addChild("rib_5", ModelPartBuilder.create().uv(12, 29).cuboid(3.0F, -17.0F, -8.0F, 0.0F, 2.0F, 2.0F), ModelTransform.of(0.0F, 14.1818F, 3.6364F, -0.3491F, 0.0F, 0.0F));
		body.addChild("rib_6", ModelPartBuilder.create().uv(0, 15).cuboid(4.0F, -20.0F, -8.0F, 0.0F, 2.0F, 3.0F), ModelTransform.of(0.0F, 14.1818F, 3.6364F, -0.3491F, 0.0F, 0.0F));
		body.addChild("rib_7", ModelPartBuilder.create().uv(56, 24).cuboid(1.0F, -20.0F, -10.0F, 3.0F, 2.0F, 2.0F), ModelTransform.of(0.0F, 14.1818F, 3.6364F, -0.3491F, 0.0F, 0.0F));
		body.addChild("rib_8", ModelPartBuilder.create().uv(56, 50).cuboid(-4.0F, -20.0F, -10.0F, 3.0F, 2.0F, 2.0F), ModelTransform.of(0.0F, 14.1818F, 3.6364F, -0.3491F, 0.0F, 0.0F));
		body.addChild("rib_9", ModelPartBuilder.create().uv(53, 48).cuboid(-4.0F, -20.0F, -5.0F, 8.0F, 2.0F, 0.0F), ModelTransform.of(0.0F, 14.1818F, 3.6364F, -0.3491F, 0.0F, 0.0F));
		body.addChild("rib_10", ModelPartBuilder.create().uv(8, 54).cuboid(-1.0F, -21.0F, -10.0F, 2.0F, 8.0F, 2.0F), ModelTransform.of(0.0F, 14.1818F, 3.6364F, -0.3491F, 0.0F, 0.0F));
		body.addChild("hip", ModelPartBuilder.create().uv(40, 42).cuboid(-4.0F, -14.0F, -9.0F, 8.0F, 2.0F, 4.0F), ModelTransform.of(0.0F, 14.1818F, 3.6364F, -0.2618F, 0.0F, 0.0F));

		ModelPartData upper_body = modelPartData.addChild(EntityModelPartNames.NECK, ModelPartBuilder.create().uv(0, 16).cuboid(-4.0F, -8.5F, 4.0F, 8.0F, 7.0F, 8.0F), ModelTransform.rotation(0.0F, 1.5F, -1.0F));
		upper_body.addChild("rib_1", ModelPartBuilder.create().uv(58, 0).cuboid(1.0F, -19.0F, -17.0F, 3.0F, 2.0F, 2.0F), ModelTransform.of(0.0F, 22.5F, 1.0F, -0.6981F, 0.0F, 0.0F));
		upper_body.addChild("rib_2", ModelPartBuilder.create().uv(56, 54).cuboid(-4.0F, -19.0F, -17.0F, 3.0F, 2.0F, 2.0F), ModelTransform.of(0.0F, 22.5F, 1.0F, -0.6981F, 0.0F, 0.0F));
		upper_body.addChild("rib_3", ModelPartBuilder.create().uv(52, 5).cuboid(-4.0F, -20.0F, -10.0F, 8.0F, 3.0F, 0.0F), ModelTransform.of(0.0F, 22.5F, 1.0F, -0.6981F, 0.0F, 0.0F));
		upper_body.addChild("rib_4", ModelPartBuilder.create().uv(0, 22).cuboid(-5.0F, -22.0F, -10.0F, 3.0F, 2.0F, 0.0F), ModelTransform.of(0.0F, 22.5F, 1.0F, -0.6981F, 0.0F, 0.0F));
		upper_body.addChild("rib_5", ModelPartBuilder.create().uv(0, 20).cuboid(2.0F, -22.0F, -10.0F, 3.0F, 2.0F, 0.0F), ModelTransform.of(0.0F, 22.5F, 1.0F, -0.6981F, 0.0F, 0.0F));
		upper_body.addChild("rib_6", ModelPartBuilder.create().uv(56, 20).cuboid(-5.0F, -22.0F, -17.0F, 4.0F, 2.0F, 2.0F), ModelTransform.of(0.0F, 22.5F, 1.0F, -0.6981F, 0.0F, 0.0F));
		upper_body.addChild("rib_7", ModelPartBuilder.create().uv(16, 56).cuboid(1.0F, -22.0F, -17.0F, 4.0F, 2.0F, 2.0F), ModelTransform.of(0.0F, 22.5F, 1.0F, -0.6981F, 0.0F, 0.0F));
		upper_body.addChild("rib_8", ModelPartBuilder.create().uv(14, 49).cuboid(5.0F, -22.0F, -15.0F, 0.0F, 2.0F, 5.0F), ModelTransform.of(0.0F, 22.5F, 1.0F, -0.6981F, 0.0F, 0.0F));
		upper_body.addChild("rib_9", ModelPartBuilder.create().uv(36, 5).cuboid(4.0F, -19.0F, -15.0F, 0.0F, 2.0F, 5.0F), ModelTransform.of(0.0F, 22.5F, 1.0F, -0.6981F, 0.0F, 0.0F));
		upper_body.addChild("rib_10", ModelPartBuilder.create().uv(36, 3).cuboid(-4.0F, -19.0F, -15.0F, 0.0F, 2.0F, 5.0F), ModelTransform.of(0.0F, 22.5F, 1.0F, -0.6981F, 0.0F, 0.0F));
		upper_body.addChild("rib_11", ModelPartBuilder.create().uv(24, 16).cuboid(-5.0F, -22.0F, -15.0F, 0.0F, 2.0F, 5.0F), ModelTransform.of(0.0F, 22.5F, 1.0F, -0.6981F, 0.0F, 0.0F));
		upper_body.addChild("rib_12", ModelPartBuilder.create().uv(0, 54).cuboid(-1.0F, -26.0F, -17.0F, 2.0F, 10.0F, 2.0F), ModelTransform.of(0.0F, 22.5F, 1.0F, -0.6981F, 0.0F, 0.0F));

		ModelPartData jaw = modelPartData.addChild(EntityModelPartNames.JAW, ModelPartBuilder.create(), ModelTransform.rotation(0.0F, 0.0F, 11.0F));
		jaw.addChild("side_teeth_1", ModelPartBuilder.create().uv(32, 23).cuboid(3.0F, -31.0F, 4.0F, 1.0F, 3.0F, 1.0F), ModelTransform.of(0.0F, 24.0F, -11.0F, -0.4363F, 0.0F, 0.0F));
		jaw.addChild("side_teeth_2", ModelPartBuilder.create().uv(0, 33).cuboid(-4.0F, -29.0F, 3.0F, 1.0F, 1.0F, 1.0F), ModelTransform.of(0.0F, 24.0F, -11.0F, -0.4363F, 0.0F, 0.0F));
		jaw.addChild("side_teeth_3", ModelPartBuilder.create().uv(46, 0).cuboid(-4.0F, -30.0F, 4.0F, 1.0F, 2.0F, 1.0F), ModelTransform.of(0.0F, 24.0F, -11.0F, -0.4363F, 0.0F, 0.0F));
		jaw.addChild("side_teeth_4", ModelPartBuilder.create().uv(16, 42).cuboid(3.0F, -29.0F, 8.0F, 1.0F, 2.0F, 1.0F), ModelTransform.of(0.0F, 24.0F, -11.0F, -0.2618F, 0.0F, 0.0F));
		jaw.addChild("side_teeth_5", ModelPartBuilder.create().uv(15, 32).cuboid(3.0F, -28.0F, 7.0F, 1.0F, 1.0F, 1.0F), ModelTransform.of(0.0F, 24.0F, -11.0F, -0.2618F, 0.0F, 0.0F));
		jaw.addChild("side_teeth_6", ModelPartBuilder.create().uv(24, 0).cuboid(-4.0F, -27.0F, 4.0F, 8.0F, 2.0F, 6.0F), ModelTransform.of(0.0F, 24.0F, -11.0F, -0.2618F, 0.0F, 0.0F));
		jaw.addChild("side_teeth_7", ModelPartBuilder.create().uv(44, 26).cuboid(-4.0F, -28.0F, 8.0F, 1.0F, 2.0F, 1.0F), ModelTransform.of(0.0F, 24.0F, -11.0F, -0.2182F, 0.0F, 0.0F));
		jaw.addChild("teeth_1", ModelPartBuilder.create().uv(36, 12).cuboid(-3.0F, -33.0F, 0.0F, 1.0F, 3.0F, 1.0F), ModelTransform.of(0.0F, 24.0F, -11.0F, -0.6109F, 0.0F, 0.0F));
		jaw.addChild("teeth_2", ModelPartBuilder.create().uv(40, 26).cuboid(-1.0F, -34.0F, 0.0F, 1.0F, 4.0F, 1.0F), ModelTransform.of(0.0F, 24.0F, -11.0F, -0.6109F, 0.0F, 0.0F));
		jaw.addChild("teeth_3", ModelPartBuilder.create().uv(24, 0).cuboid(2.0F, -34.0F, 0.0F, 1.0F, 4.0F, 1.0F), ModelTransform.of(0.0F, 24.0F, -11.0F, -0.6109F, 0.0F, 0.0F));
		jaw.addChild("teeth_4", ModelPartBuilder.create().uv(46, 8).cuboid(-2.0F, -32.0F, 0.0F, 1.0F, 2.0F, 1.0F), ModelTransform.of(0.0F, 24.0F, -11.0F, -0.6109F, 0.0F, 0.0F));
		jaw.addChild("teeth_5", ModelPartBuilder.create().uv(12, 33).cuboid(-4.0F, -31.0F, 0.0F, 1.0F, 1.0F, 1.0F), ModelTransform.of(0.0F, 24.0F, -11.0F, -0.6109F, 0.0F, 0.0F));
		jaw.addChild("teeth_6", ModelPartBuilder.create().uv(32, 46).cuboid(0.0F, -31.0F, 0.0F, 1.0F, 1.0F, 1.0F), ModelTransform.of(0.0F, 24.0F, -11.0F, -0.6109F, 0.0F, 0.0F));
		jaw.addChild("teeth_7", ModelPartBuilder.create().uv(41, 48).cuboid(1.0F, -32.0F, 0.0F, 1.0F, 2.0F, 1.0F), ModelTransform.of(0.0F, 24.0F, -11.0F, -0.6109F, 0.0F, 0.0F));
		jaw.addChild("teeth_8", ModelPartBuilder.create().uv(40, 12).cuboid(3.0F, -33.0F, 0.0F, 1.0F, 3.0F, 1.0F), ModelTransform.of(0.0F, 24.0F, -11.0F, -0.6109F, 0.0F, 0.0F));
		jaw.addChild("teeth_9", ModelPartBuilder.create().uv(44, 38).cuboid(-4.0F, -30.0F, 0.0F, 8.0F, 3.0F, 1.0F), ModelTransform.of(0.0F, 24.0F, -11.0F, -0.6109F, 0.0F, 0.0F));


		ModelPartData head = modelPartData.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create(), ModelTransform.rotation(0.0F, -5.3333F, 13.0F));
		head.addChild("head_part_1", ModelPartBuilder.create().uv(24, 8).cuboid(-6.0F, -32.0F, 12.0F, 2.0F, 5.0F, 8.0F), ModelTransform.of(0.0F, 29.3333F, -13.0F, 0.0873F, 0.0F, 0.0F));
		head.addChild("head_part_2", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -33.0F, 12.0F, 8.0F, 8.0F, 8.0F), ModelTransform.of(0.0F, 29.3333F, -13.0F, 0.0873F, 0.0F, 0.0F));
		head.addChild("head_part_3", ModelPartBuilder.create().uv(36, 13).cuboid(4.0F, -32.0F, 12.0F, 2.0F, 5.0F, 8.0F), ModelTransform.of(0.0F, 29.3333F, -13.0F, 0.0873F, 0.0F, 0.0F));

		ModelPartData right_lower_arm = modelPartData.addChild(EntityModelPartNames.RIGHT_HIND_LEG, ModelPartBuilder.create(), ModelTransform.rotation(8.5F, 2.25F, 9.0F));
		right_lower_arm.addChild("right_lower_arm_r1", ModelPartBuilder.create().uv(0, 31).cuboid(8.0F, -18.0F, 11.0F, 4.0F, 14.0F, 4.0F), ModelTransform.of(-8.5F, 21.75F, -9.0F, 0.2618F, 0.0F, -0.0873F));

		ModelPartData left_lower_arm = modelPartData.addChild(EntityModelPartNames.LEFT_HIND_LEG, ModelPartBuilder.create(), ModelTransform.rotation(-7.0F, 2.5F, 9.0F));
		left_lower_arm.addChild("left_lower_arm_r1", ModelPartBuilder.create().uv(28, 27).cuboid(-11.0F, -18.0F, 11.0F, 4.0F, 11.0F, 4.0F), ModelTransform.of(7.0F, 21.5F, -9.0F, 0.2618F, 0.0F, 0.0873F));

		ModelPartData left_upper_arm = modelPartData.addChild(EntityModelPartNames.LEFT_ARM, ModelPartBuilder.create(), ModelTransform.rotation(-3.5F, -5.5F, 5.5F));
		left_upper_arm.addChild("left_upper_arm_r1", ModelPartBuilder.create().uv(32, 48).cuboid(-18.0F, -22.0F, 10.0F, 3.0F, 9.0F, 3.0F), ModelTransform.of(3.5F, 29.5F, -5.5F, 0.2618F, 0.0F, 0.48F));


		ModelPartData right_upper_arm = modelPartData.addChild(EntityModelPartNames.RIGHT_ARM, ModelPartBuilder.create(), ModelTransform.rotation(4.0F, -5.0F, 6.0F));
		right_upper_arm.addChild("right_upper_arm_r1", ModelPartBuilder.create().uv(48, 8).cuboid(15.0F, -22.0F, 10.0F, 3.0F, 9.0F, 3.0F), ModelTransform.of(-4.0F, 29.0F, -6.0F, 0.2618F, 0.0F, -0.48F));




		return TexturedModelData.of(modelData, 128, 128);
	 */


}