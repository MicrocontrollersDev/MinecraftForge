package net.minecraftforge.client.model.pipeline;

import net.minecraft.util.MathHelper;


public class VertexLighterSmoothAo extends VertexLighterFlat {
    @Override
    protected void updateLightmap(float[] normal, float[] lightmap, float x, float y, float z) {
        calcLightmap(lightmap, x, y, z);
    }

    @Override
    protected void updateColor(float[] normal, float[] color, float x, float y, float z, float tint, int multiplier) {
        super.updateColor(normal, color, x, y, z, tint, multiplier);
        float a = getAo(x, y, z);
        color[0] *= a;
        color[1] *= a;
        color[2] *= a;
    }

    protected void calcLightmap(float[] lightmap, float x, float y, float z) {
        x *= 2;
        y *= 2;
        z *= 2;
        float l2 = x * x + y * y + z * z;
        if (l2 > 6 - 2e-2f) {
            float s = (float) Math.sqrt((6 - 2e-2f) / l2);
            x *= s;
            y *= s;
            z *= s;
        }
        float ax = x > 0 ? x : -x;
        float ay = y > 0 ? y : -y;
        float az = z > 0 ? z : -z;
        float e1 = 1 + 1e-4f;
        if (ax > 2 - 1e-4f && ay <= e1 && az <= e1) {
            x = x < 0 ? -2 + 1e-4f : 2 - 1e-4f;
        } else if (ay > 2 - 1e-4f && az <= e1 && ax <= e1) {
            y = y < 0 ? -2 + 1e-4f : 2 - 1e-4f;
        } else if (az > 2 - 1e-4f && ax <= e1 && ay <= e1) {
            z = z < 0 ? -2 + 1e-4f : 2 - 1e-4f;
        }
        ax = x > 0 ? x : -x;
        ay = y > 0 ? y : -y;
        az = z > 0 ? z : -z;
        if (ax <= e1 && ay + az > 3f - 1e-4f) {
            float s = (3f - 1e-4f) / (ay + az);
            y *= s;
            z *= s;
        } else if (ay <= e1 && az + ax > 3f - 1e-4f) {
            float s = (3f - 1e-4f) / (az + ax);
            z *= s;
            x *= s;
        } else if (az <= e1 && ax + ay > 3f - 1e-4f) {
            float s = (3f - 1e-4f) / (ax + ay);
            x *= s;
            y *= s;
        } else if (ax + ay + az > 4 - 1e-4f) {
            float s = (4 - 1e-4f) / (ax + ay + az);
            x *= s;
            y *= s;
            z *= s;
        }

        float[][][][] blockLight = blockInfo.getBlockLight();
        float[][][][] skyLight = blockInfo.getSkyLight();

        float bl = 0f;
        float sl = 0f;
        float s = 0f;

        for (int ix = 0; ix <= 1; ix++) {
            for (int iy = 0; iy <= 1; iy++) {
                for (int iz = 0; iz <= 1; iz++) {
                    float vx = x * (1 - ix * 2);
                    float vy = y * (1 - iy * 2);
                    float vz = z * (1 - iz * 2);

                    float s3 = vx + vy + vz + 4;
                    float sx = vy + vz + 3;
                    float sy = vz + vx + 3;
                    float sz = vx + vy + 3;

                    float bx = (2 * vx + vy + vz + 6) / (s3 * sy * sz * (vx + 2));
                    s += bx;
                    bl += bx * blockLight[0][ix][iy][iz];
                    sl += bx * skyLight[0][ix][iy][iz];

                    float by = (2 * vy + vz + vx + 6) / (s3 * sz * sx * (vy + 2));
                    s += by;
                    bl += by * blockLight[1][ix][iy][iz];
                    sl += by * skyLight[1][ix][iy][iz];

                    float bz = (2 * vz + vx + vy + 6) / (s3 * sx * sy * (vz + 2));
                    s += bz;
                    bl += bz * blockLight[2][ix][iy][iz];
                    sl += bz * skyLight[2][ix][iy][iz];
                }
            }
        }

        bl /= s;
        sl /= s;

        lightmap[0] = MathHelper.clamp_float(bl, 0f, 15f * 0x20 / 0xFFFF);
        lightmap[1] = MathHelper.clamp_float(sl, 0f, 15f * 0x20 / 0xFFFF);
    }

    protected float getAo(float x, float y, float z) {
        int sx = x < 0 ? 1 : 2;
        int sy = y < 0 ? 1 : 2;
        int sz = z < 0 ? 1 : 2;

        if (x < 0) x++;
        if (y < 0) y++;
        if (z < 0) z++;

        float a = 0;
        float[][][] ao = blockInfo.getAo();
        a += ao[sx - 1][sy - 1][sz - 1] * (1 - x) * (1 - y) * (1 - z);
        a += ao[sx - 1][sy - 1][sz] * (1 - x) * (1 - y) * (0 + z);
        a += ao[sx - 1][sy][sz - 1] * (1 - x) * (0 + y) * (1 - z);
        a += ao[sx - 1][sy][sz] * (1 - x) * (0 + y) * (0 + z);
        a += ao[sx][sy - 1][sz - 1] * (0 + x) * (1 - y) * (1 - z);
        a += ao[sx][sy - 1][sz] * (0 + x) * (1 - y) * (0 + z);
        a += ao[sx][sy][sz - 1] * (0 + x) * (0 + y) * (1 - z);
        a += ao[sx][sy][sz] * (0 + x) * (0 + y) * (0 + z);

        a = MathHelper.clamp_float(a, 0, 1);
        return a;
    }

    @Override
    public void updateBlockInfo() {
        blockInfo.updateShift();
        blockInfo.updateLightMatrix();
    }
}
