package com.zmachsoft.gameoflife.world;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.zmachsoft.gameoflife.world.boids.Boid;
import com.zmachsoft.gameoflife.world.boids.Obstacle;
import com.zmachsoft.gameoflife.world.boids.Vector;
import com.zmachsoft.gameoflife.world.setting.BoidsSetting;
import com.zmachsoft.gameoflife.world.setting.ExcitableMediaSetting;
import com.zmachsoft.gameoflife.world.setting.WorldSetting;

import java.util.Arrays;
import java.util.Random;

import de.biomedical_imaging.edu.wlu.cs.levy.CG.KDTree;

/**
 * Details in
 *
 * @author Master
 */
public class WorldBoids extends GameWorld {

    private Boid[] boids;
    private static final Paint BOID_PAINT = new Paint();
    private static final Paint OBSTACLE_PAINT = new Paint();

    private double cohesionCoefficient;
    private int alignmentCoefficient;
    private double separationCoefficient;
    private int distance;
    private int velocityMax;
    private KDTree<Boid> kd;
    private Obstacle[] obstacles;

    public WorldBoids() {
        super(new ExcitableMediaSetting());
    }

    public WorldBoids(WorldSetting setting) {
        super(setting);
    }

    @Override
    public void initContent() {
        Log.i("GOL", "World init data");
        Log.i("GOL", "Nb tiles : " + setting.getNbTiles());
        Log.i("GOL", "Nb of boids : " + ((BoidsSetting) setting).getNbBoids());
        Log.i("GOL", "size of board for boids : " + getBoardWidth() + " / " + getBoardheight());

        Random random = new Random(System.currentTimeMillis());
        int nbBoids = ((BoidsSetting) setting).getNbBoids();
        boids = new Boid[nbBoids];
        this.kd = new KDTree<>(2);
        for (int i = 0; i < nbBoids; i++) {
            double x = random.nextInt(getBoardWidth());
            double y = random.nextInt(getBoardheight());
//            try {
            Boid boid = new Boid(new Vector(x, y), new Vector(0, 0)); // no initial velocity
//                kd.insert(boid.position.data, boid);
            boids[i] = boid;
//            } catch (KeySizeException e) {
//                throw new RuntimeException(e);
//            } catch (KeyDuplicateException e) {
//                i = i - 1; // we skipped this one - let's compute one more
//            }
        }

        // create some obstacles
        int nbObstacles = 2;
        obstacles = new Obstacle[nbObstacles];
        for (int i = 0; i < nbObstacles; i++) {
            double x = random.nextInt(getBoardWidth());
            double y = random.nextInt(getBoardheight());
            obstacles[i] = new Obstacle(new Vector(x, y));
        }

        // come from settings
        this.cohesionCoefficient = ((BoidsSetting) setting).getCohesionCoefficient();
        this.alignmentCoefficient = ((BoidsSetting) setting).getAlignmentCoefficient();
        this.separationCoefficient = ((BoidsSetting) setting).getSeparationCoefficient();
        this.distance = ((BoidsSetting) setting).getDistance();
        this.velocityMax = ((BoidsSetting) setting).getVelocityMax();

        BOID_PAINT.setColor(Color.BLACK);
        OBSTACLE_PAINT.setColor(Color.RED);
        OBSTACLE_PAINT.setStyle(Paint.Style.FILL);

        Log.i("GOL", "KD tree contains " + kd.size() + " items");
    }

    @Override
    public void nextStep() throws NoChangeException {
        Log.i("GOL", "World next step");

        Arrays.stream(boids)
                .forEach(boid -> {
                    Boid[] neighbours = null;
                    Obstacle[] closeObstacles = null;
                    try {
//                        List<Boid> nearest = kd.nearest(coords, distance);
//                        neighbours = nearest.toArray(new Boid[0]);
                        neighbours = findNeighbours(boid);
                        closeObstacles = findCloseObstacles(boid);
//                        kd.delete(coords);
                    } catch (Exception ignore) {
                        // we ignore this exception on purpose
//                        System.out.println("KeyMissingException deleting caught: " + ignore + ignore.getMessage());
                    }
                    boid.updateVelocity(neighbours, closeObstacles, cohesionCoefficient, alignmentCoefficient, separationCoefficient, velocityMax);
                    boid.updatePosition();
                });

        //the implementation of deletion in KdTree does not actually delete nodes,
        //but only marks them, that affects performance, so it's necessary to rebuild the tree
        //after long sequences of insertions and deletions        kd = new KDTree<>(2);
//        try {
//            for (int i = 0; i < boids.length - 1; i++) {
//                kd.insert(boids[i].position.data, boids[i]);
//            }
//        } catch (KeySizeException | KeyDuplicateException e) {
//            throw new RuntimeException(e);
//        }
    }

    @Override
    /**
     * Render the world to an off-screen bitmap
     */
    public void render(Canvas canvas) {
        Log.i("GD", "Surface onDraw");
        Arrays.stream(boids)
                .forEach(boid -> render(boid, canvas));
        Arrays.stream(obstacles)
                .forEach(obstacle -> render(obstacle, canvas));
    }

    private void render(Boid boid, Canvas canvas) {
        // project boid's coordinates into display referential (limited to width / height)
        int x = boid.getX() > 0 ? boid.getX() % getBoardWidth() : getBoardWidth() - Math.abs(boid.getX()) % getBoardWidth();
        int y = boid.getY() > 0 ? boid.getY() % getBoardheight() : getBoardheight() - Math.abs(boid.getY()) % getBoardheight();

        canvas.drawCircle(x, y, 4, BOID_PAINT);
    }

    private void render(Obstacle obstacle, Canvas canvas) {
        // project boid's coordinates into display referential (limited to width / height)
        int x = obstacle.getX() > 0 ? obstacle.getX() % getBoardWidth() : getBoardWidth() - Math.abs(obstacle.getX()) % getBoardWidth();
        int y = obstacle.getY() > 0 ? obstacle.getY() % getBoardheight() : getBoardheight() - Math.abs(obstacle.getY()) % getBoardheight();

        canvas.drawCircle(x, y, 40, OBSTACLE_PAINT);
    }

    @Override
    public String toString() {
        return "Boids media - id=" + uniqueId;
    }

    private Boid[] findNeighbours(Boid boid) {
        double minX = boid.position.data[0] - distance;
        double maxX = boid.position.data[0] + distance;
        double minY = boid.position.data[1] - distance;
        double maxY = boid.position.data[1] + distance;
        return Arrays.stream(boids)
                .filter(b -> b.uniqueId != boid.uniqueId)
                .filter(b -> b.position.data[0] >= minX && b.position.data[0] <= maxX
                        && b.position.data[1] >= minY && b.position.data[1] <= maxY)
                .toArray(Boid[]::new);
    }

    private Obstacle[] findCloseObstacles(Boid boid) {
        double minX = boid.position.data[0] - distance;
        double maxX = boid.position.data[0] + distance;
        double minY = boid.position.data[1] - distance;
        double maxY = boid.position.data[1] + distance;
        return Arrays.stream(obstacles)
                .filter(o -> o.position.data[0] >= minX && o.position.data[0] <= maxX
                        && o.position.data[1] >= minY && o.position.data[1] <= maxY)
                .toArray(Obstacle[]::new);
    }
}

