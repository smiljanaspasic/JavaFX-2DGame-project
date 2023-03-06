package com.example.golfer;

import com.example.golfer.objects.*;
import com.example.golfer.widgets.ProgressBar;
import com.example.golfer.widgets.Status;
import javafx.animation.Animation;
import javafx.animation.PathTransition;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main extends Application implements EventHandler<MouseEvent> {
	private static final double WINDOW_WIDTH = 600.0;
	private static final double WINDOW_HEIGHT = 800.0;
	private static final double PLAYER_WIDTH = 20.0;
	private static final double PLAYER_HEIGHT = 80.0;
	private static final double PLAYER_MAX_ANGLE_OFFSET = 60.0;
	private static final double PLAYER_MIN_ANGLE_OFFSET = -60.0;
	private static final double MS_IN_S = 1000.0;
	private static final double NS_IN_S = 1.0E9;
	private static final double MAXIMUM_HOLD_IN_S = 3.0;
	private static double MAXIMUM_BALL_SPEED = 1500.0;
	private static final double BALL_RADIUS = 5.0;
	private static final double BALL_DAMP_FACTOR = 0.995;
	private static final double MIN_BALL_SPEED = 1.0;
	private static final double HOLE_RADIUS = 15.0;
	private static final double PROGRESS_BAR_WIDTH = 12.0;
	private static final double OBSTACLE_WIDTH = 120.0;
	private static final double OBSTACLE_HEIGHT = 16.0;
	private static final double FENCE_WIDTH = 18.0;
	private static final double SPEED_MODIFIER_WIDTH = 30.0;
	private static final double MUD_DAMP_FACTOR = 0.9;
	private static final double ICE_DAMP_FACTOR = 1.1;

	private static final double MAXIMUM_HOLE_SPEED = 400.0;
	private static final int NUMBER_OF_LIVES = 5;
	private static final int HOLE_POINTS_MAXIMUM = 20;
	private static final int HOLE_POINTS_MEDIUM = 10;
	private static final int HOLE_POINTS_MINIMUM = 5;

	public static final double MAX_TIME = 100;
	
	private Group root;
	private Player player;
	private Ball ball;
	private long time;
	private Hole holes[];
	private ProgressBar progressBar;

	private Obstacle[] obstacles;

	private Fence fence;
	private SpeedModifier[] mudPuddles;
	private SpeedModifier[] icePatches;

	private Image myBackground;

	private Rectangle timeLeft;

	private Ufo ufos[];
	public static double timePassed = MAX_TIME;

	private ArrayList<Coin> coins;

	private enum State
	{
		IDLE,
		PREPARATION,
		BALL_SHOT,
		FALLING; }
	private State state;
	private Status status;

	boolean startTimer=false;

	private TeleportField[] teleportFields;

	private void addUfos() {
		final double ufoRad = WINDOW_WIDTH / 10;

		double posX = -WINDOW_WIDTH;
		double posY1 = WINDOW_HEIGHT * 2 / 3;

		double posY2 = WINDOW_HEIGHT / 2;


		Image ufoPattern = new Image(Main.class.getClassLoader().getResourceAsStream("etf.jpg"));
		ImagePattern ufoImage = new ImagePattern(ufoPattern);

		Path path1 = new Path(
				new MoveTo(0, 0),
				new LineTo(3 * WINDOW_WIDTH, 0),
				new VLineTo(WINDOW_HEIGHT),
				new LineTo(0, WINDOW_HEIGHT),
				new ClosePath()
		);

		Path path2 = new Path(
				new MoveTo(0, 0),
				new LineTo(WINDOW_WIDTH + WINDOW_WIDTH / 2, -WINDOW_HEIGHT / 2 + ufoRad + FENCE_WIDTH),
				new HLineTo(3 * WINDOW_WIDTH),
				new VLineTo(3*WINDOW_HEIGHT),
				new ClosePath()
		);
		Path path3 = new Path(
				new MoveTo(0,0),
				new HLineTo(4*WINDOW_HEIGHT/2.6),
				new ArcTo(WINDOW_HEIGHT*2,WINDOW_HEIGHT*2,WINDOW_HEIGHT*2,WINDOW_HEIGHT*2,WINDOW_WIDTH,false,false),
				new ClosePath()
		);

		Ufo ufo1 = new Ufo(ufoRad, new Translate(posX, posY1), ufoImage);

		Ufo ufo2 = new Ufo(ufoRad, new Translate(posX, posY2), ufoImage);

		Ufo ufo3=new Ufo(ufoRad,new Translate(posX,posY1/6),ufoImage);

		PathTransition anim1 = new PathTransition();
		anim1.setDuration(Duration.seconds(25));
		anim1.setNode(ufo1);
		anim1.setPath(path1);
		anim1.setCycleCount(Animation.INDEFINITE);
		anim1.play();

		PathTransition anim2 = new PathTransition();
		anim2.setDuration(Duration.seconds(35));
		anim2.setNode(ufo2);
		anim2.setPath(path2);
		anim2.setCycleCount(Animation.INDEFINITE);
		anim2.play();

		PathTransition anim3 = new PathTransition();
		anim3.setDuration(Duration.seconds(10));
		anim3.setNode(ufo3);
		anim3.setPath(path3);
		anim3.setCycleCount(Animation.INDEFINITE);
		anim3.play();

		this.root.getChildren().addAll(ufo1, ufo2,ufo3);

		this.ufos = new Ufo[]{
				ufo1,
				ufo2,
				ufo3
		};

	}
	private void addTeleports(){
		TeleportField teleport= new TeleportField(250,500);
		TeleportField teleport1= new TeleportField(490,295);
		teleport.setMyPair(teleport1);
		teleport1.setMyPair(teleport);
		this.root.getChildren().addAll(teleport,teleport1);
		teleportFields=new TeleportField[]{
				teleport,
				teleport1
		};
	}
	private void addCoin() {
		final double coinRad = WINDOW_WIDTH / 70;
		int randomEffect = (int) (Math.random() * 3 + 1);

		double posX = Math.random() * (WINDOW_WIDTH - 2 * FENCE_WIDTH - coinRad) + (FENCE_WIDTH + coinRad);
		double posY = Math.random() * (WINDOW_HEIGHT - 2 * FENCE_WIDTH - coinRad) + (FENCE_WIDTH + coinRad);

		Translate pos = new Translate(posX, posY);
		Coin coin = new Coin(coinRad, pos, randomEffect);
		boolean collidedWithObstacle = Arrays.stream(this.obstacles).anyMatch(obstacle -> coin.handleCollision(obstacle.getBoundsInParent()));
		boolean inMud = Arrays.stream(this.mudPuddles).anyMatch(mudPuddle -> mudPuddle.handleCollision(coin));
		boolean isOverIce = Arrays.stream(this.icePatches).anyMatch(icePatch -> icePatch.handleCollision(coin));
		boolean isOverHole = Arrays.stream(this.holes).anyMatch(hole -> hole.handleCollision(coin));
		boolean isOverTeleportField= Arrays.stream(this.teleportFields).anyMatch(teleportField -> teleportField.handleCollision(coin));
		if (!inMud && !isOverIce && !collidedWithObstacle && !isOverHole && !isOverTeleportField ) {
			coins.add(coin);
			this.root.getChildren().addAll(coin);
		}


	}
	private void addSpeedModifiers() {
		final Translate mudPuddle0Position = new Translate(105.0, 105.0);
		final SpeedModifier mudPuddle0 = SpeedModifier.mud(30.0, 30.0, mudPuddle0Position);
		this.root.getChildren().addAll(mudPuddle0);
		final Translate icePatch0Position = new Translate(465.0, 105.0);
		final SpeedModifier icePatch0 = SpeedModifier.ice(SPEED_MODIFIER_WIDTH, SPEED_MODIFIER_WIDTH, icePatch0Position);
		this.root.getChildren().addAll(icePatch0);
		final Translate mudPuddle1Position = new Translate(465.0, 545.0);
		final SpeedModifier mudPuddle2 = SpeedModifier.mud(SPEED_MODIFIER_WIDTH, SPEED_MODIFIER_WIDTH, mudPuddle1Position);
		this.root.getChildren().addAll(mudPuddle2);
		final Translate icePatch1Position = new Translate(105.0, 545.0);
		final SpeedModifier icePatch2 = SpeedModifier.ice(SPEED_MODIFIER_WIDTH, SPEED_MODIFIER_WIDTH, icePatch1Position);
		this.root.getChildren().addAll(icePatch2);
		this.mudPuddles = new SpeedModifier[] { mudPuddle0, mudPuddle2 };
		this.icePatches = new SpeedModifier[] { icePatch0, icePatch2 };
	}
	private void addObstacles() {
		final Translate obstacle0Position = new Translate(140.0, WINDOW_HEIGHT/2);
		final Obstacle obstacle0 = new Obstacle(OBSTACLE_WIDTH, OBSTACLE_HEIGHT, obstacle0Position);
		this.root.getChildren().addAll(obstacle0);
		final Translate obstacle1Position = new Translate(340.0, WINDOW_HEIGHT/2);
		final Obstacle obstacle2 = new Obstacle(OBSTACLE_WIDTH, OBSTACLE_HEIGHT, obstacle1Position);
		this.root.getChildren().addAll(obstacle2);
		final Translate obstacle2Position = new Translate(292.0, 136.0);
		final Obstacle obstacle3 = new Obstacle(OBSTACLE_HEIGHT, OBSTACLE_WIDTH, obstacle2Position);
		this.root.getChildren().addAll(obstacle3);
		this.obstacles = new Obstacle[] { obstacle0, obstacle2, obstacle3 };
	}
	private void addHoles ( ) {
		Translate hole0Position = new Translate (
				Main.WINDOW_WIDTH / 2,
				Main.WINDOW_HEIGHT * 0.1
		);
		Hole hole0 = new Hole ( Main.HOLE_RADIUS, hole0Position, Color.DARKGOLDENROD, HOLE_POINTS_MAXIMUM );
		this.root.getChildren ( ).addAll ( hole0 );
		
		Translate hole1Position = new Translate (
				Main.WINDOW_WIDTH / 2,
				Main.WINDOW_HEIGHT * 0.4
		);
		Hole hole1 = new Hole ( Main.HOLE_RADIUS, hole1Position,Color.LIGHTGREEN,HOLE_POINTS_MINIMUM );
		this.root.getChildren ( ).addAll ( hole1 );
		
		Translate hole2Position = new Translate (
				Main.WINDOW_WIDTH / 3,
				Main.WINDOW_HEIGHT * 0.25
		);
		Hole hole2 = new Hole ( Main.HOLE_RADIUS, hole2Position,Color.YELLOW,HOLE_POINTS_MEDIUM );
		this.root.getChildren ( ).addAll ( hole2 );
		
		Translate hole3Position = new Translate (
				Main.WINDOW_WIDTH * 2 / 3,
				Main.WINDOW_HEIGHT * 0.25
		);
		Hole hole3 = new Hole ( Main.HOLE_RADIUS, hole3Position,Color.YELLOW,HOLE_POINTS_MEDIUM );
		this.root.getChildren ( ).addAll ( hole3 );
		
		this.holes = new Hole[] {
				hole0,
				hole1,
				hole2,
				hole3,
		};
	}
	public void endAttempt() {
		this.root.getChildren().remove(this.ball);
		this.ball = null;
		this.state = State.IDLE; }

	@Override
	public void start ( Stage stage ) throws IOException {
		this.state= State.IDLE;
		this.root  = new Group ( );

        //izbor terena

        Text text = new Text("IZBOR TERENA");
        text.setFont(Font.font("verdana", 50));
        text.getTransforms().addAll(
                new Translate(WINDOW_WIDTH/5, WINDOW_HEIGHT / 7)
        );
        text.setFill(Color.LIGHTGREEN);

        Button rubber = new Button("Crvena guma");
		rubber.setFont(Font.font("verdana", 50));
        rubber.getTransforms().addAll(
                new Translate(WINDOW_WIDTH / 5, WINDOW_HEIGHT / 4)
        );

        Button stone = new Button("Kamen");
		stone.setFont(Font.font("verdana", 50));
        stone.getTransforms().addAll(
                new Translate(WINDOW_WIDTH / 5, WINDOW_HEIGHT / 2)
        );

        Button grass = new Button("Trava");
		grass.setFont(Font.font("verdana", 50));
        grass.getTransforms().addAll(
                new Translate(WINDOW_WIDTH / 5, WINDOW_HEIGHT * 3 / 4)
        );

        Group terrainPick = new Group();
		terrainPick.getChildren().addAll(grass, stone, rubber, text);

        Image menuPattern = new Image(Main.class.getClassLoader().getResourceAsStream("fence.jpg"));
        ImagePattern menuBackground = new ImagePattern(menuPattern);

        Scene mainMenu = new Scene(terrainPick, Main.WINDOW_WIDTH, WINDOW_HEIGHT, menuBackground);

        stage.setScene(mainMenu);

		Image backgroundImg = new Image(Main.class.getClassLoader().getResourceAsStream("grass.jpg"));
		ImagePattern background = new ImagePattern(backgroundImg);

		//izbor topa

		Text textCannons = new Text("IZBOR TOPA");
		textCannons.setFont(Font.font("verdana", 50));
		textCannons.getTransforms().addAll(
				new Translate(WINDOW_WIDTH / 5, WINDOW_HEIGHT / 7)
		);
		textCannons.setFill(Color.MAGENTA);


		Button slow = new Button("Spori top");
		slow.setFont(Font.font("verdana", 50));
		slow.getTransforms().addAll(
				new Translate(WINDOW_WIDTH /5, WINDOW_HEIGHT / 4)
		);

		Button medium = new Button("Srednji top");
		medium.setFont(Font.font("verdana", 50));
		medium.getTransforms().addAll(
				new Translate(WINDOW_WIDTH / 5, WINDOW_HEIGHT / 2)
		);

		Button fast = new Button("Brzi top");
		fast.setFont(Font.font("verdana", 50));
		fast.getTransforms().addAll(
				new Translate(WINDOW_WIDTH / 5, WINDOW_HEIGHT * 3 / 4)
		);

		Group cannonChoice = new Group();
		cannonChoice.getChildren().addAll(slow, medium, fast, textCannons);

		Scene canonMenu = new Scene(cannonChoice, Main.WINDOW_WIDTH, WINDOW_HEIGHT, background);

		//

		
		Scene scene = new Scene ( this.root, Main.WINDOW_WIDTH, WINDOW_HEIGHT, background);
		final Image fenceImage = new Image(Main.class.getClassLoader().getResourceAsStream("fence.jpg"));
		final ImagePattern fenceImagePattern = new ImagePattern(fenceImage);
		this.fence = new Fence(WINDOW_WIDTH, WINDOW_HEIGHT, FENCE_WIDTH, fenceImagePattern);
		this.root.getChildren().addAll(this.fence);
		Translate playerPosition = new Translate (
				Main.WINDOW_WIDTH / 2 - Main.PLAYER_WIDTH / 2,
				Main.WINDOW_HEIGHT - Main.PLAYER_HEIGHT
		);

		//Vreme
		timeLeft = new Rectangle(WINDOW_WIDTH / 30, WINDOW_HEIGHT);
		timeLeft.getTransforms().addAll(
				new Translate(WINDOW_WIDTH - WINDOW_WIDTH / 30, 0)
		);
		timeLeft.setFill(Color.RED);
		this.root.getChildren().addAll(timeLeft);

		Text timeText = new Text("" + (int) timePassed);
		timeText.setFill(Color.WHITE);
		timeText.setStyle("-fx-font:12 verdana;");
		timeText.getTransforms().addAll(
				new Translate(WINDOW_WIDTH - WINDOW_WIDTH / 37, WINDOW_HEIGHT / 20)
		);
		this.root.getChildren().addAll(timeText);
		//

		this.player = new Player (
				Main.PLAYER_WIDTH,
				Main.PLAYER_HEIGHT,
				playerPosition,
				1
		);
		// event driven promena topa
		slow.setOnAction(actionEvent -> {
			this.player = new Player(
					Main.PLAYER_WIDTH,
					Main.PLAYER_HEIGHT,
					playerPosition,
					0
			);
			this.MAXIMUM_BALL_SPEED = 700;
			stage.setScene(scene);
			this.root.getChildren ( ).addAll ( this.player );
		});

		medium.setOnAction(actionEvent -> {
			this.player = new Player(
					Main.PLAYER_WIDTH,
					Main.PLAYER_HEIGHT,
					playerPosition,
					1
			);
			this.MAXIMUM_BALL_SPEED =1500;
			stage.setScene(scene);
			this.root.getChildren ( ).addAll ( this.player );

		});

		fast.setOnAction(actionEvent -> {
			this.player = new Player(
					Main.PLAYER_WIDTH,
					Main.PLAYER_HEIGHT,
					playerPosition,
					2
			);
			this.MAXIMUM_BALL_SPEED=2100;
			stage.setScene(scene);
			this.root.getChildren ( ).addAll ( this.player );
		});


		this.progressBar = new ProgressBar(PROGRESS_BAR_WIDTH, WINDOW_HEIGHT);
		this.root.getChildren().addAll(this.progressBar);
		this.coins=new ArrayList<>();
		this.addHoles ( );
		this.addObstacles();
		this.addSpeedModifiers();
		this.addTeleports();
		this.addCoin();
		this.addUfos();
		this.status = new Status(NUMBER_OF_LIVES, BALL_RADIUS, WINDOW_WIDTH);
		this.root.getChildren().addAll(this.status);
		scene.addEventHandler (
				MouseEvent.MOUSE_MOVED,
				mouseEvent -> this.player.handleMouseMoved (
						mouseEvent,
						Main.PLAYER_MIN_ANGLE_OFFSET,
						Main.PLAYER_MAX_ANGLE_OFFSET
				)
		);
		
		scene.addEventHandler ( MouseEvent.ANY, this );
		scene.addEventHandler(KeyEvent.KEY_PRESSED, this::handleKeyPressed);

		//event driven promena bacgrounda
		rubber.setOnAction(actionEvent -> {
			myBackground = new Image(Main.class.getClassLoader().getResourceAsStream("rubber.jpg"));
			ImagePattern backgroundImage = new ImagePattern(myBackground);
			scene.setFill(backgroundImage);
			stage.setScene(canonMenu);
		});

		stone.setOnAction(actionEvent -> {
			myBackground = new Image(Main.class.getClassLoader().getResourceAsStream("stone.jpg"));
			ImagePattern backgroundImage = new ImagePattern(myBackground);
			scene.setFill(backgroundImage);
			stage.setScene(canonMenu);
		});

		grass.setOnAction(actionEvent -> {
			myBackground = new Image(Main.class.getClassLoader().getResourceAsStream("grass.jpg"));
			ImagePattern backgroundImage = new ImagePattern(myBackground);
			scene.setFill(backgroundImage);
			stage.setScene(canonMenu);
		});

		//

		Timer timer = new Timer(
				deltaNanoseconds -> {
					double deltaSeconds = ( double ) deltaNanoseconds / Main.NS_IN_S;

					if (startTimer) {
						timePassed -= deltaSeconds;
						timeText.setText("" + (int) timePassed);
						timeText.setTranslateY(WINDOW_HEIGHT - timeLeft.getHeight());
					}
					if (timePassed <= 0) stage.close();
					timeLeft.setHeight((timePassed / MAX_TIME) * WINDOW_HEIGHT);
					timeLeft.setTranslateY(WINDOW_HEIGHT - timeLeft.getHeight());
					int random = (int) (Math.random() * 10000 + 1);
					if (random < 50)
					{ 		addCoin(); }
					if (this.state == State.BALL_SHOT) {
						boolean collidedWithObstacle = Arrays.stream(this.obstacles).anyMatch(obstacle -> this.ball.handleCollision(obstacle.getBoundsInParent()));
						double dampFactor=BALL_DAMP_FACTOR;
						boolean inMud = Arrays.stream(this.mudPuddles).anyMatch(mudPuddle -> mudPuddle.handleCollision(this.ball));
						boolean isOverIce = Arrays.stream(this.icePatches).anyMatch(icePatch -> icePatch.handleCollision(this.ball));
						if (inMud) {
							dampFactor = MUD_DAMP_FACTOR;
						}
						else if (isOverIce) {
							dampFactor = ICE_DAMP_FACTOR;
						}
						boolean isTeleported = Arrays.stream(this.teleportFields).anyMatch(teleportField -> teleportField.handleCollision(this.ball));
						if(isTeleported) {
						this.teleportFields[0].setVisited(false);
						 }
						if (coins.size() > 0) {
							for (int i = 0; i < coins.size(); ++i) {
								boolean notimeLeft = coins.get(i).timesUp(deltaSeconds);
								if (notimeLeft) {
									this.root.getChildren().remove(coins.get(i));
									coins.remove(i);
								}
							}
						}

								if (coins.size() > 0) {
									for (int i = 0; i < coins.size(); ++i) {
										boolean hit = coins.get(i).handleCollision(this.ball);
										if (hit) {
											if (coins.get(i).coinEffect == 1) {
												this.status.addPoints(10);
											} else if (coins.get(i).coinEffect == 2) {
												this.status.addLife(BALL_RADIUS,WINDOW_WIDTH);
											} else if (coins.get(i).coinEffect == 3) {
												if (Main.timePassed + 10 <= Main.MAX_TIME)
													Main.timePassed += 10;
												else
													Main.timePassed = Main.MAX_TIME;
											}
											this.root.getChildren().remove(coins.get(i));
											coins.remove(i);
										}
									}
								}


						boolean stopped = this.ball.update(deltaSeconds, 18.0, 582.0, 18.0, 782.0, dampFactor, MIN_BALL_SPEED);
						Optional<Hole> ohole = Arrays.stream(this.holes).filter(hole -> hole.handleCollision(this.ball)).findFirst();
						if (stopped) {
							this.root.getChildren().remove(this.ball);
							this.ball = null;
							this.state = State.IDLE;
						}
						if (ohole.isPresent() && this.ball.getSpeedMagnitude() <= MAXIMUM_HOLE_SPEED) {
							this.ball.initializeFalling(event -> {
								this.status.addPoints(ohole.get().getPoints());
								this.root.getChildren().remove(this.ball);
								this.state = State.IDLE;
								return;
							});
							this.state = State.FALLING;}
						if(this.ball!=null) {
							boolean hitUfo = Arrays.stream(this.ufos).anyMatch(ufo -> ufo.handleCollision(this.ball));
							if (hitUfo) {
								endAttempt();
							}
						}
					}
					else if (this.state == State.PREPARATION) {
						this.progressBar.elapsed(deltaNanoseconds, NS_IN_S*3);
					}
					return;
				});


		timer.start ( );
		
		scene.setCursor ( Cursor.NONE );
		
		stage.setTitle ( "Golfer" );
		stage.setResizable ( false );
		stage.show ( );
	}
	
	public static void main ( String[] args ) {
		launch ( );
	}
	
	@Override public void handle ( MouseEvent mouseEvent ) {
		if (this.status.hasLives()) {
		if (this.state == State.IDLE && mouseEvent.getEventType().equals(MouseEvent.MOUSE_PRESSED) && mouseEvent.isPrimaryButtonDown()) {
			this.time = System.currentTimeMillis();
			this.startTimer=true;
			this.progressBar.onClick();
			this.state = State.PREPARATION;
		} else if (this.state == State.PREPARATION && mouseEvent.getEventType ( ).equals ( MouseEvent.MOUSE_RELEASED ) ) {

				double value        = ( System.currentTimeMillis ( ) - this.time ) / Main.MS_IN_S;
				double deltaSeconds = Utilities.clamp ( value, 0, Main.MAXIMUM_HOLD_IN_S );
				
				double ballSpeedFactor = deltaSeconds / Main.MAXIMUM_HOLD_IN_S * Main.MAXIMUM_BALL_SPEED;
				
				Translate ballPosition = this.player.getBallPosition ( );
				Point2D   ballSpeed    = this.player.getSpeed ( ).multiply ( ballSpeedFactor );
				
				this.ball = new Ball ( Main.BALL_RADIUS, ballPosition, ballSpeed );
				this.root.getChildren ( ).addAll ( this.ball );
				this.progressBar.onRelease();
				this.status.removeLife();
				this.state = State.BALL_SHOT;
			}

		}
	}

	public void handleKeyPressed(KeyEvent keyEvent) {
		if (this.ball != null && keyEvent.getCode() == KeyCode.SPACE) {
			this.endAttempt();
		}
	}

	}

