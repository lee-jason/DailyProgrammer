(function simulation() {
	var map;
	var mapSize;
	var mapArea;
	var monthTick;
	var bearTick;
	var lumberjackTick;
	var displayTickRate;
	var INITIAL_LUMBERJACK_SPAWN_PERCENT = .10;
	var INITIAL_TREE_SPAWN_PERCENT = .50;
	var INITIAL_BEAR_SPAWN_PERCENT = .02;
	var NORMALTREE_SAPLING_SPAWN_CHANCE = .10;
	var ELDERTREE_SAPLING_SPAWN_CHANCE = .20;
	var SAPLING_MONTHS_TO_MATURE = 12;
	var NORMALTREE_MONTHS_TO_MATURE = 120;
	var BEAR_MOVEMENTS_PER_MONTH = 5;
	var LUMBERJACK_MOVEMENTS_PER_MONTH = 3;
	var DISPLAY_TICK_RATE_PER_MONTH = 15;
	var locationOfEvents = [];
	var init = function(_mapSize, _monthTick) {
		mapSize = _mapSize;
		mapArea = mapSize * mapSize;
		monthTick = _monthTick;
		bearTick = monthTick / BEAR_MOVEMENTS_PER_MONTH;
		lumberjackTick = monthTick / LUMBERJACK_MOVEMENTS_PER_MONTH;
		displayTickRate = monthTick / DISPLAY_TICK_RATE_PER_MONTH;

		map = new Map();

		var spawnInitialEntities = function() {
			var initialLumberjacks = Math.ceil(mapArea
					* INITIAL_LUMBERJACK_SPAWN_PERCENT);
			var initialNormalTrees = Math.ceil(mapArea
					* INITIAL_TREE_SPAWN_PERCENT);
			var initialBears = Math.ceil(mapArea * INITIAL_BEAR_SPAWN_PERCENT);
			for (var i = 0; i < initialLumberjacks; i++) {
				map.createNewEntity(EntityEnum.Lumberjack);
			}
			for (var i = 0; i < initialNormalTrees; i++) {
				map.createNewEntity(EntityEnum.NormalTree);
			}
			for (var i = 0; i < initialBears; i++) {
				map.createNewEntity(EntityEnum.Bear);
			}
		}

		spawnInitialEntities();
		map.printMap();
		setInterval(function(){map.monthlyEvents()}, monthTick);
		setInterval(function(){map.moveEntities(EntityEnum.Lumberjack)},lumberjackTick);
		setInterval(function(){map.moveEntities(EntityEnum.Bear)}, bearTick);
		setInterval(function(){map.printMap()}, displayTickRate);
		setInterval(function(){map.clearConsole()}, monthTick * 60);
	}

	//in hindsight, I think I can refactor away the logic from the map.
	//map should have just been used for object placement.
	var Map = function() {
		var that = this;
		var Bears = [];
		var Lumberjacks = [];
		var NormalTrees = [];
		var ElderTrees = [];
		var Saplings = [];
		var conflictCoordinates = [];
		var mawCount = 0;
		var lumberCollected = 0;
		var mapArray;
		var monthCount = 0;
		mapArray = new Array(mapSize);
		for (var i = 0; i < mapSize; i++) {
			mapArray[i] = new Array(mapSize);
			for (var j = 0; j < mapSize; j++) {
				mapArray[i][j] = [];
			}
		}
		
		//trigger sapling chance events
		//trigger bear spawn/removal event
		//trigger lumberjack spawn/removal events
		this.monthlyEvents = function(){
			if(monthCount % 12 == 0 && monthCount != 0){
				yearlyEvents();
			}
			//else monthly events
			else{
				for(var i = 0; i < Bears.length; i++){
					Bears[i].moveable = true;
				}
				for(var i = 0; i < Lumberjacks.length; i++){
					Lumberjacks[i].moveable = true;
				}
				for(var i = 0; i < Saplings.length; i++){
					Saplings[i].age++;
					if(Saplings[i].age >= SAPLING_MONTHS_TO_MATURE){
						upgradeTree(Saplings[i]);
					}
				}
				for(var i = 0; i < NormalTrees.length; i++){
					NormalTrees[i].age++;
					if(Math.random() < NORMALTREE_SAPLING_SPAWN_CHANCE){
						placeEntityAroundCoord(EntityEnum.Sapling, {x: NormalTrees[i].pos.x, y: NormalTrees[i].pos.y});
					}
					if(NormalTrees[i].age >= NORMALTREE_MONTHS_TO_MATURE){
						upgradeTree(NormalTrees[i]);
					}
				}
				for(var i = 0; i < ElderTrees.length; i++){
					if(Math.random() < ELDERTREE_SAPLING_SPAWN_CHANCE){
						placeEntityAroundCoord(EntityEnum.Sapling, {x: ElderTrees[i].pos.x, y: ElderTrees[i].pos.y});
					}
					ElderTrees[i].age++;
				}
			}
			monthCount++;
		}
		
		var upgradeTree = function(tree){
			var treePos = {x: tree.pos.x, y: tree.pos.y};
			var treeType = tree.type;
			destroyEntity(tree.type, treePos);
			if(tree.type == EntityEnum.Sapling){
				that.createNewEntity(EntityEnum.NormalTree, treePos);
			}
			else if(tree.type == EntityEnum.NormalTree){
				that.createNewEntity(EntityEnum.ElderTree, treePos);
			}
			
		}
		
		var yearlyEvents = function(){
			//# of lumberjacks to spawn
			var jacksToSpawn;
			if(Math.floor(lumberCollected / Lumberjacks.length) > 0){
				jacksToSpawn = Math.floor(lumberCollected / Lumberjacks.length);
			}
			else if(Lumberjacks.length > 1){
				destroyEntity(EntityEnum.lumberjack, {x: Lumberjacks[Lumberjacks.length - 1].pos.x, y: Lumberjacks[Lumberjacks.length - 1].pos.y});
			}
			for(var i = 0; i < jacksToSpawn; i++){
				that.createNewEntity(EntityEnum.Lumberjack);
			}
			
			//# of bears to spawn
			if(mawCount == 0){
				that.createNewEntity(EntityEnum.Bear);
			}
			else{
				destroyEntity(EntityEnum.Bear, {x: Bears[Bears.length - 1].pos.x, y: Bears[Bears.length - 1].pos.y});
			}
			
			lumberCollected = 0;
			mawCount = 0;
		}

		this.createNewEntity = function(entityType, _pos) {
			var pos = {};
			if(_pos){
				pos.x = _pos.x;
				pos.y = _pos.y;
			}
			else{
				pos = nextAvailableSpaceOnMap({
					x : Helper.randomNumber(mapSize),
					y : Helper.randomNumber(mapSize)
				});
			}

			// if no space
			if (!pos) {
				return;
			}
			switch (entityType) {
			case EntityEnum.Sapling:
				var tempSapling = new Sapling(pos);
				Saplings.push(tempSapling);
				mapArray[pos.y][pos.x].push(tempSapling);
				break;
			case EntityEnum.NormalTree:
				var tempNormalTree = new NormalTree(pos);
				NormalTrees.push(tempNormalTree);
				mapArray[pos.y][pos.x].push(tempNormalTree);
				break;
			case EntityEnum.ElderTree:
				var tempElderTree = new ElderTree(pos);
				ElderTrees.push(tempElderTree);
				mapArray[pos.y][pos.x].push(tempElderTree);
				break;
			case EntityEnum.Lumberjack:
				var tempLumberjack = new Lumberjack(pos);
				Lumberjacks.push(tempLumberjack);
				mapArray[pos.y][pos.x].push(tempLumberjack);
				break;
			case EntityEnum.Bear:
				var tempBear = new Bear(pos);
				Bears.push(tempBear);
				mapArray[pos.y][pos.x].push(tempBear);
				break;
			}
		}
		// mainly used by spawn sapling I think
		var placeEntityAroundCoord = function(entityType, coord) {
			var emptyCoord = nextAvailableEmptySpaceAroundCoord(coord);
			
			if(emptyCoord){
				switch (entityType) {
				case EntityEnum.Sapling:
					that.createNewEntity(entityType, emptyCoord);
					break;
				}
			}
			return false;
		}
		
		this.moveEntities = function(entityType) {
			if (entityType == EntityEnum.Lumberjack) {
				for (var i = 0; i < Lumberjacks.length; i++) {
					if(Lumberjacks[i].moveable){
						var newCoord = moveEntity(Lumberjacks[i]);
						for (var j = 0; j < mapArray[newCoord.y][newCoord.x].length; j++) {
							if (mapArray[newCoord.y][newCoord.x][j].type == EntityEnum.Bear) {
								destroyEntity(EntityEnum.Lumberjack, newCoord);
								mapArray[newCoord.y][newCoord.x][j].moveable = false;
							} else if (mapArray[newCoord.y][newCoord.x][j].type == EntityEnum.NormalTree) {
								destroyEntity(EntityEnum.NormalTree, newCoord);
								Lumberjacks[i].moveable = false;
							} else if (mapArray[newCoord.y][newCoord.x][j].type == EntityEnum.ElderTree) {
								destroyEntity(EntityEnum.ElderTree, newCoord);
								Lumberjacks[i].moveable = false;
							}
						}
					}
				}
			} else if (entityType == EntityEnum.Bear) {
				for (var i = 0; i < Bears.length; i++) {
					if(Bears[i].moveable){
						var newCoord = moveEntity(Bears[i]);
						for (var j = 0; j < mapArray[newCoord.y][newCoord.x].length; j++) {
							if (mapArray[newCoord.y][newCoord.x][j].type == EntityEnum.Lumberjack) {
								destroyEntity(EntityEnum.Lumberjack, newCoord);
								Bears[i].moveable = false;
							}
						}
					}
				}
			}
		}
		
		this.yearlyReview = function(){
			
		}

		this.getCoord = function(coord) {
			return mapArray[coord.y][coord.x];
		}
		
		this.printMap = function() {
			var mapString = [];
			for (var y = 0; y < mapSize; y++) {
				for (var x = 0; x < mapSize; x++) {
					var highestEntity = -1;
					if (mapArray[y][x].length > 0) {
						for (var i = 0; i < mapArray[y][x].length; i++) {
							if (mapArray[y][x][i].type > highestEntity) {
								highestEntity = mapArray[y][x][i].type;
							}
						}
					}
					switch (highestEntity) {
					case EntityEnum.Sapling:
						mapString.push('s');
						break;
					case EntityEnum.NormalTree:
						mapString.push('T');
						break;
					case EntityEnum.ElderTree:
						mapString.push('E');
						break;
					case EntityEnum.Lumberjack:
						mapString.push('L');
						break;
					case EntityEnum.Bear:
						mapString.push('B');
						break;
					default:
						mapString.push(' ');
					}
				}
				mapString.push('\r\n');
			}
			mapString.push('\r\n month: ' + monthCount);
			mapString.push('\r\n maw: ' + mawCount);
			mapString.push('\r\n lumber collected: ' + lumberCollected);
			mapString.push('\r\n lumberjacks: ' + Lumberjacks.length);
			mapString.push('\r\n bears: ' + Bears.length);
			mapString.push('\r\n saplings: ' + Saplings.length);
			mapString.push('\r\n normal trees: ' + NormalTrees.length);
			mapString.push('\r\n elder trees: ' + ElderTrees.length);
			console.log(mapString.join(''));
		}
		
		this.clearConsole = function(){
			console.clear();
		}

		var moveEntity = function(entity) {
			var nextPos = nextAvailableSpaceAroundCoord(entity.type, entity.pos);
			// remove entity from current position and move it to the new
			// position
			mapArray[entity.pos.y][entity.pos.x].splice(mapArray[entity.pos.y][entity.pos.x].indexOf(entity), 1);
			entity.pos = nextPos;
			mapArray[nextPos.y][nextPos.x].push(entity);
			return nextPos;
		}

		var destroyEntity = function(entityType, coord) {
			if (entityType == EntityEnum.Lumberjack) {
				mawCount++;
			} else if (entityType == EntityEnum.NormalTree) {
				lumberCollected++;
			} else if (entityType == EntityEnum.ElderTree) {
				lumberCollected += 2;
			} else if (entityType == EntityEnum.Bear) {

			}
			//remove them from the mapArray as well as from the object array
			for (var i = 0; i < mapArray[coord.y][coord.x].length; i++) {
				if (mapArray[coord.y][coord.x][i].type == entityType) {
					if(entityType == EntityEnum.Lumberjack){
						Lumberjacks.splice(Lumberjacks.indexOf(mapArray[coord.y][coord.x][i]), 1);
					}
					else if(entityType == EntityEnum.Bear){
						Bears.splice(Bears.indexOf(mapArray[coord.y][coord.x][i]), 1);
					}
					else if(entityType == EntityEnum.Sapling){
						Saplings.splice(Saplings.indexOf(mapArray[coord.y][coord.x][i]), 1);
					}
					else if(entityType == EntityEnum.NormalTree){
						NormalTrees.splice(NormalTrees.indexOf(mapArray[coord.y][coord.x][i]), 1);
					}
					else if(entityType == EntityEnum.ElderTree){
						ElderTrees.splice(ElderTrees.indexOf(mapArray[coord.y][coord.x][i]), 1);
					}
					mapArray[coord.y][coord.x].splice(i, 1);
				}
			}
		}

		//TODO: make it so that this looks behind as well, bfs?
		var nextAvailableSpaceOnMap = function(coord) {
			for (var y = coord.y; y < mapSize; y++) {
				for (var x = coord.x; x < mapSize; x++) {
					if (coordIsEmpty({
						x : x,
						y : y
					})) {
						return {
							x : x,
							y : y
						};
					}
				}
			}
			return false;
		}
		// [0][1][2]
		// [3][x][4]
		// [5][6][7]
		// mainly used by new sprouts
		var nextAvailableEmptySpaceAroundCoord = function(coord) {
			var gridPos = Helper.randomNumber(8);
			var tempCoord = {
				x : -1,
				y : -1
			};

			for (var i = 0; i < 8; i++) {
				gridPos = (i + gridPos) % 8;

				switch (gridPos) {
				case 0:
					tempCoord = {
						x : coord.x - 1,
						y : coord.y - 1
					};
					break;
				case 1:
					tempCoord = {
						x : coord.x,
						y : coord.y - 1
					};
					break;
				case 2:
					tempCoord = {
						x : coord.x + 1,
						y : coord.y - 1
					};
					break;
				case 3:
					tempCoord = {
						x : coord.x - 1,
						y : coord.y
					};
					break;
				case 4:
					tempCoord = {
						x : coord.x + 1,
						y : coord.y
					};
					break;
				case 5:
					tempCoord = {
						x : coord.x - 1,
						y : coord.y + 1
					};
					break;
				case 6:
					tempCoord = {
						x : coord.x,
						y : coord.y + 1
					};
					break;
				case 7:
					tempCoord = {
						x : coord.x + 1,
						y : coord.y + 1
					};
					break;
				}

				if (coordInBounds(tempCoord) && coordIsEmpty(tempCoord)) {
					return tempCoord;
				}
			}

			return false;
		}

		// mainly used by things that can move
		var nextAvailableSpaceAroundCoord = function(entityType, coord) {
			var gridPos = Helper.randomNumber(8);
			var tempCoord = {
				x : -1,
				y : -1
			};

			gridIterate_loop: for (var i = 0; i < 8; i++) {
				gridPos = (i+gridPos) % 8;

				switch (gridPos) {
				case 0:
					tempCoord = {
						x : coord.x - 1,
						y : coord.y - 1
					};
					break;
				case 1:
					tempCoord = {
						x : coord.x,
						y : coord.y - 1
					};
					break;
				case 2:
					tempCoord = {
						x : coord.x + 1,
						y : coord.y - 1
					};
					break;
				case 3:
					tempCoord = {
						x : coord.x - 1,
						y : coord.y
					};
					break;
				case 4:
					tempCoord = {
						x : coord.x + 1,
						y : coord.y
					};
					break;
				case 5:
					tempCoord = {
						x : coord.x - 1,
						y : coord.y + 1
					};
					break;
				case 6:
					tempCoord = {
						x : coord.x,
						y : coord.y + 1
					};
					break;
				case 7:
					tempCoord = {
						x : coord.x + 1,
						y : coord.y + 1
					};
					break;
				}

				if (coordInBounds(tempCoord)) {
					//verify that there's not a same type of entity in the planned next space
					for (var j = 0; j < mapArray[tempCoord.y][tempCoord.x].length; j++) {
						if (mapArray[tempCoord.y][tempCoord.x][j].type == entityType) {
							break gridIterate_loop;
						}
					}
					return tempCoord;
				}
			}

			return coord;
		}
		var coordIsEmpty = function(coord) {
			if (mapArray[coord.y][coord.x].length == 0) {
				return true;
			} else {
				return false;
			}
		}

		var coordInBounds = function(coord) {
			if ((coord.x >= 0 && coord.x < mapSize) && (coord.y >= 0
					&& coord.y < mapSize)) {
				return true;
			}
			return false;
		}
	}

	var EntityEnum = {
		Sapling : 0,
		NormalTree : 1,
		ElderTree : 2,
		Lumberjack : 3,
		Bear : 4
	}

	var Entity = function(_pos) {
		this.pos = _pos;
		this.type;
	}
	
	// MoveableEntity extends Entity
	var MoveableEntity = function(_pos){
		Entity.call(this, _pos);
		this.moveable = true;
	}
	MoveableEntity.prototype = Object.create(Entity.prototype);
	MoveableEntity.prototype.constructor = MoveableEntity;

	// Tree extends Entity
	var Tree = function(pos) {
		Entity.call(this, pos);
		this.age = 0;
		var SaplingSpawnChance = .00;

		this.getSaplingSpawnChance = function() {
			return SaplingSpawnChance;
		}
	}
	Tree.prototype = Object.create(Entity.prototype);
	Tree.prototype.constructor = Tree;

	// Sapling extends Tree
	var Sapling = function(pos) {
		Tree.call(this, pos);
		this.type = EntityEnum.Sapling;
	}
	Sapling.prototype = Object.create(Tree.prototype);
	Sapling.prototype.constructor = Sapling;

	// NormalTree extends Tree
	var NormalTree = function(pos) {
		Tree.call(this, pos);
		this.type = EntityEnum.NormalTree;
		var SaplingSpawnChance = NORMALTREE_SAPLING_SPAWN_CHANCE;
	}
	NormalTree.prototype = Object.create(Tree.prototype);
	NormalTree.prototype.constructor = NormalTree;

	// ElderTree extends Tree
	var ElderTree = function(pos) {
		Tree.call(this, pos);
		this.type = EntityEnum.ElderTree;
		var SaplingSpawnChance = ELDERTREE_SAPLING_SPAWN_CHANCE;
	}
	ElderTree.prototype = Object.create(Tree.prototype);
	ElderTree.prototype.constructor = ElderTree;

	// Lumberjack extends MoveableEntity
	var Lumberjack = function(pos) {
		MoveableEntity.call(this, pos);
		this.type = EntityEnum.Lumberjack;
	}
	Lumberjack.prototype = Object.create(MoveableEntity.prototype);
	Lumberjack.prototype.constructor = Lumberjack;

	// Bear extends MoveableEntity
	var Bear = function(pos) {
		MoveableEntity.call(this, pos);
		this.type = EntityEnum.Bear;
	}
	Bear.prototype = Object.create(MoveableEntity.prototype);
	Bear.prototype.constructor = Bear;

	// well this doesn't work for some reason.
	var Helper = {
		randomNumber : function(upperLimit) {
			return Math.floor(Math.random() * upperLimit);
		}
	}

	init(25, 100);
})();