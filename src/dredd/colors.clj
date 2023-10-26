(ns dredd.colors
  (:require [clojure.string :as str]))

(defn- escape [i] (str "\033[" i "m"))
(def ^:dynamic *disable-colors* false)
(def reset (escape 0))

(defn- apply-color [color-code args]
  (if *disable-colors*
    (apply str args)
    (str
      (str/join (map #(str color-code %) args))
      reset)))

(defn bold          [& args] (apply-color "[1m" args))
(defn dark          [& args] (apply-color "[2m" args))
(defn underline     [& args] (apply-color "[4m" args))
(defn blink         [& args] (apply-color "[5m" args))
(defn reverse-color [& args] (apply-color "[7m" args))
(defn concealed     [& args] (apply-color "[8m" args))

(defn gray          [& args] (apply-color "[30m" args))
(defn grey          [& args] (apply-color "[30m" args))
(defn red           [& args] (apply-color "[31m" args))
(defn green         [& args] (apply-color "[32m" args))
(defn yellow        [& args] (apply-color "[33m" args))
(defn blue          [& args] (apply-color "[34m" args))
(defn magenta       [& args] (apply-color "[35m" args))
(defn cyan          [& args] (apply-color "[36m" args))
(defn white         [& args] (apply-color "[37m" args))

(defn on-grey       [& args] (apply-color "[40m" args))
(defn on-red        [& args] (apply-color "[41m" args))
(defn on-green      [& args] (apply-color "[42m" args))
(defn on-yellow     [& args] (apply-color "[43m" args))
(defn on-blue       [& args] (apply-color "[44m" args))
(defn on-magenta    [& args] (apply-color "[45m" args))
(defn on-cyan       [& args] (apply-color "[46m" args))
(defn on-white      [& args] (apply-color "[47m" args))


(def fruits ["apple" "chokeberry" "hawthorn" "juneberry" "loquat" "pear"
             "quince" "almond" "apricot" "avocado" "coconut" "coffeeberry"
             "crowberry" "date" "guavaberry" "guarana" "hackberry" "jujube"
             "longan" "lychee" "mango" "nectarine" "partridgeberry" "peach"
             "rambutan" "sourplum" "banana" "bearberry" "blueberry" "cranberry"
             "elderberry" "gooseberry" "grape" "guava" "honeysuckle"
             "huckleberry" "kiwi" "lingonberry""mayapple" "papaya"
             "passionfruit" "pepino" "persimmon" "plantain" "pomegranate"
             "muskmelon" "watermelon" "citron" "clementine" "grapefruit"
             "kumquat" "lemon" "lime" "limeberry" "limequat" "melogold" "orange"
             "pomelo" "tangelo" "tangerine" "yuzu" "blackberry" "boysenberry"
             "cherimoya" "cloudberry" "dewberry" "loganberry" "pineberry"
             "salmonberry" "strawberry" "wineberry" "youngberry" "fig"
             "jackfruit" "pineapple" "durian" "mangosteen" "vanilla" "tamarind"
             "rhubarb" "tomato"])

(def animals
  ["aardvark" "aardwolf" "abyssinian" "addax" "affenpinscher" "agouti" "aidi"
  "ainu" "airedoodle" "akbash" "akita" "albatross" "albertonectes" "allosaurus"
  "allosaurus" "alpaca" "alusky" "amargasaurus" "amberjack" "anaconda"
  "anchovies" "andrewsarchus" "angelfish" "angelshark" "anglerfish" "anhinga"
  "anomalocaris" "ant" "anteater" "antelope" "anteosaurus" "ape"
  "arambourgiania" "arapaima" "archaeoindris" "archaeopteryx" "archaeotherium"
  "archerfish" "arctodus" "arctotherium" "argentinosaurus" "armadillo"
  "armyworm" "arsinoitherium" "arthropleura" "asp" "aurochs" "aussiedoodle"
  "aussiedor" "aussiepom" "australopithecus" "avocet" "axolotl" "azawakh"
  "babirusa" "baboon" "badger" "baiji" "balinese" "bandicoot" "barb" "barbet"
  "barinasuchus" "barnacle" "barnevelder" "barosaurus" "barracuda" "barylambda"
  "basilosaurus" "bass" "bassador" "bassetoodle" "bat" "batfish" "baya"
  "beabull" "beagador" "beagle" "beaglier" "beago" "bear" "beaski" "beauceron"
  "beaver" "bee" "beefalo" "beetle" "bergamasco" "bernedoodle" "bichir"
  "bichpoo" "bilby" "binturong" "bird" "birman" "bison" "blobfish" "bloodhound"
  "blowfly" "bluefish" "bluegill" "boas" "bobcat" "bobolink" "boerboel" "boggle"
  "boiga" "bombay" "bonefish" "bongo" "bonobo" "booby" "boomslang" "borador"
  "bordoodle" "borkie" "boskimo" "bowfin" "boxachi" "boxador" "boxerdoodle"
  "boxfish" "boxsky" "boxweiler" "brachiosaurus" "briard" "brittany"
  "brontosaurus" "brug" "budgerigar" "buffalo" "bullboxer" "bulldog" "bullfrog"
  "bullmastiff" "bullsnake" "bumblebee" "burmese" "butterfly" "caecilian"
  "caiman" "camel" "cantil" "canvasback" "capuchin" "capybara" "caracal"
  "cardinal" "caribou" "carp" "cascabel" "cassowary" "cat" "caterpillar"
  "catfish" "cavador" "cavapoo" "centipede" "cephalaspis" "ceratopsian"
  "ceratosaurus" "chameleon" "chamois" "chartreux" "cheagle" "cheetah"
  "chickadee" "chicken" "chigger" "chihuahua" "chilesaurus" "chimaera"
  "chimpanzee" "chinchilla" "chinook" "chipit" "chipmunk" "chipoo" "chiton"
  "chiweenie" "chorkie" "chusky" "cicada" "cichlid" "clownfish" "coati" "cobras"
  "cockalier" "cockapoo" "cockatiel" "cockatoo" "cockle" "cockroach" "codfish"
  "coelacanth" "collie" "compsognathus" "conure" "copperhead" "coral" "corella"
  "corgidor" "corgipoo" "corkie" "cormorant" "coryphodon" "cottonmouth" "cougar"
  "cow" "coyote" "crab" "crane" "crayfish" "cricket" "crocodile"
  "crocodylomorph" "crow" "cryolophosaurus" "cuckoo" "cuttlefish" "dachsador"
  "dachshund" "daeodon" "dalmadoodle" "dalmador" "dalmatian" "damselfish"
  "daniff" "danios" "daug" "deer" "deinocheirus" "deinosuchus" "desmostylus"
  "dhole" "dickcissel" "dickinsonia" "dilophosaurus" "dimetrodon" "dingo"
  "dinocrocuta" "dinofelis" "dinopithecus" "dinosaurs" "diplodocus" "diprotodon"
  "discus" "dobsonfly" "dodo" "doedicurus" "dog" "dolphin" "donkey" "dorgi"
  "dorkie" "dormouse" "douc" "doxiepoo" "doxle" "dragonfish" "dragonfly"
  "dreadnoughtus" "drever" "duck" "dugong" "dunker" "dunkleosteus" "dunnock"
  "eagle" "earthworm" "earwig" "echidna" "eel" "eelpout" "egret" "eider" "eland"
  "elasmosaurus" "elasmotherium" "elephant" "elk" "embolotherium" "emu"
  "epidexipteryx" "ermine" "eryops" "escolar" "eskipoo" "euoplocephalus"
  "eurasier" "eurypterus" "falcon" "fangtooth" "feist" "ferret" "finch"
  "firefly" "fish" "flamingo" "flea" "flounder" "fly" "flycatcher" "fossa" "fox"
  "frenchton" "frengle" "frigatebird" "frog" "frogfish" "frug" "gadwall" "gar"
  "gastornis" "gazelle" "gecko" "genet" "gerbil" "gharial" "gibbon"
  "gigantopithecus" "giraffe" "glechon" "glowworm" "gnat" "goat" "goberian"
  "goldador" "goldcrest" "goldendoodle" "goldfish" "gollie" "gomphotherium"
  "goose" "gopher" "goral" "gorgosaurus" "gorilla" "goshawk" "gourami"
  "grasshopper" "grebe" "greyhound" "griffonshire" "groenendael" "grouper"
  "grouse" "grunion" "guppy" "haddock" "hagfish" "haikouichthys" "hainosaurus"
  "halibut" "hallucigenia" "hamster" "hare" "harrier" "hartebeest"
  "hatzegopteryx" "havamalt" "havanese" "havapoo" "havashire" "havashu" "hawk"
  "hedgehog" "helicoprion" "hellbender" "heron" "herrerasaurus" "herring"
  "himalayan" "hippopotamus" "hogfish" "hokkaido" "hoopoe" "horgi" "hornbill"
  "hornet" "horse" "horsefly" "housefly" "hovasaurus" "hovawart" "human"
  "hummingbird" "huntaway" "huskador" "huskita" "husky" "huskydoodle"
  "hyaenodon" "hyena" "ibex" "ibis" "icadyptes" "ichthyosaurus" "ichthyostega"
  "iguana" "iguanodon" "impala" "inchworm" "indri" "insect" "insects" "jabiru"
  "jacana" "jackabee" "jackal" "jackdaw" "jackrabbit" "jagdterrier" "jaguar"
  "javanese" "jellyfish" "jerboa" "junglefowl" "kagu" "kakapo" "kangaroo"
  "katydid" "kea" "keagle" "keelback" "keeshond" "kestrel" "kiang" "killdeer"
  "killifish" "kingfisher" "kingklip" "kinkajou" "kishu" "kiwi" "klipspringer"
  "knifefish" "koala" "kodkod" "komondor" "kooikerhondje" "koolie" "kouprey"
  "kowari" "krait" "krill" "kudu" "kuvasz" "labahoula" "labmaraner" "labrabull"
  "labradane" "labradoodle" "labraheeler" "labrottie" "ladybug" "ladyfish"
  "lamprey" "lancetfish" "leech" "leedsichthys" "lemming" "lemur" "leonberger"
  "leopard" "leptocephalus" "lhasapoo" "liger" "limpet" "linnet" "lion"
  "lionfish" "liopleurodon" "liopleurodon" "livyatan" "lizard" "lizardfish"
  "llama" "loach" "lobster" "locust" "lorikeet" "loris" "lowchen" "lumpfish"
  "lungfish" "lurcher" "lynx" "lyrebird" "lystrosaurus" "macaque" "macaw"
  "machaeroides" "macrauchenia" "maggot" "magpie" "magyarosaurus" "maiasaura"
  "malchi" "mallard" "malteagle" "maltese" "maltipom" "maltipoo" "mamba"
  "manatee" "mandrill" "margay" "markhor" "marmoset" "marmot" "masiakasaurus"
  "massasauga" "mastador" "mastiff" "mauzer" "mayfly" "meagle" "mealybug"
  "meerkat" "megalania" "megalochelys" "megalodon" "meganeura" "megatherium"
  "meiolania" "merganser" "microraptor" "miki" "milkfish" "millipede" "mink"
  "mockingbird" "mojarra" "mole" "mollusk" "molly" "mongoose" "mongrel" "monkey"
  "monkfish" "moorhen" "moose" "morkie" "mosasaurus" "mosquito" "moth" "mouse"
  "mudi" "mudpuppy" "mudskipper" "mule" "muntjac" "muskox" "muskrat"
  "muttaburrasaurus" "muttaburrasaurus" "nabarlek" "naegleria" "narwhal"
  "natterjack" "nautilus" "neanderthal" "nebelung" "needlefish" "newfoundland"
  "newfypoo" "newt" "nightingale" "nightjar" "nilgai" "norrbottenspets"
  "nudibranch" "numbat" "nuralagus" "nuthatch" "nutria" "nyala" "oarfish"
  "ocelot" "octopus" "oilfish" "okapi" "olingo" "olm" "onager" "opabinia" "opah"
  "opossum" "orangutan" "oribi" "ornithocheirus" "ornithomimus" "osprey"
  "ostracod" "ostrich" "otter" "otterhound" "ovenbird" "oviraptor" "owl" "ox"
  "oxpecker" "oyster" "pachycephalosaurus" "paddlefish" "pademelon" "palaeophis"
  "paleoparadoxia" "pangolin" "panther" "papillon" "parakeet" "parasaurolophus"
  "parrot" "parrotfish" "parrotlet" "partridge" "patagotitan" "peacock" "peagle"
  "peekapoo" "pekingese" "pelagornis" "pelagornithidae" "pelican" "pelycosaurs"
  "penguin" "persian" "pheasant" "phorusrhacos" "phytosaurs" "pig" "pigeon"
  "pika" "pinfish" "pipefish" "piranha" "pitador" "pitsky" "platybelodon"
  "platypus" "plesiosaur" "pliosaur" "pointer" "polacanthus" "polecat" "pomapoo"
  "pomchi" "pomeagle" "pomeranian" "pomsky" "poochon" "poodle" "poogle"
  "porcupine" "porcupinefish" "possum" "potoo" "potoroo" "prawn" "procoptodon"
  "pronghorn" "psittacosaurus" "psittacosaurus" "pteranodon" "pterodactyl"
  "pudelpointer" "puertasaurus" "pufferfish" "puffin" "pug" "pugapoo" "puggle"
  "pugshire" "puli" "puma" "pumi" "purussaurus" "pyrador" "pyredoodle"
  "pyrosome" "python" "quagga" "quail" "quetzal" "quokka" "quoll" "rabbit"
  "raccoon" "ragamuffin" "ragdoll" "raggle" "rat" "rattlesnake" "redstart"
  "reindeer" "repenomamus" "rhamphosuchus" "rhea" "rhinoceros" "roadrunner"
  "robin" "rockfish" "rodents" "rooster" "rotterman" "rottle" "rottsky"
  "rottweiler" "sable" "saiga" "sailfish" "salamander" "salmon" "saluki"
  "sambar" "samoyed" "sandpiper" "sandworm" "saola" "sapsali" "sarcosuchus"
  "sardines" "sarkastodon" "sarplaninac" "sauropoda" "sauropoda" "sawfish"
  "scallops" "schapendoes" "schipperke" "schneagle" "schnoodle" "scorpion"
  "sculpin" "scutosaurus" "seagull" "seahorse" "seal" "serval" "seymouria"
  "shantungosaurus" "shark" "shastasaurus" "sheep" "sheepadoodle" "shepadoodle"
  "shepkita" "shepweiler" "shichi" "shikoku" "shiranian" "shollie" "shrew"
  "shrimp" "siamese" "siberian" "siberpoo" "sidewinder" "simbakubwa"
  "sinosauropteryx" "sivatherium" "skua" "skunk" "sloth" "slug" "smilosuchus"
  "snail" "snailfish" "snake" "snorkie" "snowshoe" "somali" "spanador" "sparrow"
  "sparrowhawk" "sphynx" "spider" "spinosaurus" "sponge" "springador"
  "springbok" "springerdoodle" "squid" "squirrel" "squirrelfish" "stabyhoun"
  "starfish" "stingray" "stoat" "stonechat" "stonefish" "stork" "stromatolite"
  "stupendemys" "sturgeon" "styracosaurus" "suchomimus" "suckerfish"
  "supersaurus" "superworm" "surgeonfish" "swallow" "swan" "swordfish" "taipan"
  "takin" "tamarin" "tamaskan" "tang" "tapir" "tarantula" "tarbosaurus" "tarpon"
  "tarsier" "tenrec" "termite" "terrier" "tetra" "thalassomedon"
  "thanatosdrakon" "therizinosaurus" "theropod" "thrush" "thylacoleo"
  "thylacosmilus" "tick" "tiffany" "tiger" "tiktaalik" "titanoboa" "titanosaur"
  "toadfish" "torkie" "tornjak" "tortoise" "tosa" "toucan" "towhee" "toxodon"
  "treecreeper" "treehopper" "triggerfish" "troodon" "tropicbird" "trout"
  "tuatara" "tuna" "turaco" "turkey" "turnspit" "turtles" "tusoteuthis"
  "tylosaurus" "uakari" "uguisu" "uintatherium" "umbrellabird" "urial"
  "utonagan" "vaquita" "vegavis" "velociraptor" "vinegaroon" "viper" "viperfish"
  "vizsla" "vole" "vulture" "waimanu" "wallaby" "walrus" "warbler" "warthog"
  "wasp" "waterbuck" "weasel" "weimaraner" "weimardoodle" "westiepoo" "whimbrel"
  "whinchat" "whippet" "whiting" "whoodle" "wildebeest" "wiwaxia" "wolf"
  "wolffish" "wolverine" "wombat" "woodlouse" "woodpecker" "woodrat" "worm"
  "wrasse" "wryneck" "xenacanthus" "xenoceratops" "xenoposeidon"
  "xenotarsosaurus" "xerus" "xiaosaurus" "xiaotingia" "xiongguanlong"
  "xiphactinus" "xoloitzcuintli" "yabby" "yak" "yarara" "yellowhammer"
  "yellowthroat" "yoranian" "yorkiepoo" "zebra" "zebu" "zokor" "zonkey" "zorse"
  "zuchon"])

(def crayola-colors
  ["aero" "almond" "amber" "amethyst" "apricot" "aqua" "aquamarine" "azure"
   "beaver" "black" "blue" "bluetiful" "blush" "bronze" "brown" "burgundy"
   "byzantine" "camel" "canary" "cardinal" "carmine" "celeste" "champagne"
   "charcoal" "chestnut" "citron" "coffee" "copper" "coral" "cordovan" "corn"
   "cream" "crimson" "cyan" "denim" "desert" "eggplant" "eggshell" "emerald"
   "firebrick" "flame" "flax" "frostbite" "fuchsia" "fulvous" "goldenrod"
   "green" "gunmetal" "heliotrope" "iceberg" "indigo" "ivory" "jasmine" "lava"
   "lemon" "lilac" "linen" "magenta" "magnolia" "mahogany" "mandarin" "mauve"
   "melon" "midnight" "mint" "mulberry" "mustard" "olive" "olivine" "onyx"
   "opal" "orange" "orchid" "oxblood" "peach" "pear" "pink" "pistachio"
   "platinum" "plum" "prune" "pumpkin" "purple" "purpureus" "raspberry"
   "razzmatazz" "red" "rose" "ruby" "rust" "saffron" "sage" "salmon" "sand"
   "sapphire" "scarlet" "seashell" "sepia" "shadow" "sienna" "silver" "snow"
   "straw" "strawberry" "tan" "tangerine" "taupe" "teal" "tomato" "turquoise"
   "ultramarine" "vanilla" "vermilion" "violet" "viridian" "white" "wine"
   "yellow"])

(defn rand-name [& [f]]
  "When provided, calls f on the random name."
  (cond-> (str (rand-nth crayola-colors) " " (rand-nth fruits)) f f))
