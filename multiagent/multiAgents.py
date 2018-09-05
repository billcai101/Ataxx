# multiAgents.py
# --------------
# Licensing Information:  You are free to use or extend these projects for
# educational purposes provided that (1) you do not distribute or publish
# solutions, (2) you retain this notice, and (3) you provide clear
# attribution to UC Berkeley, including a link to http://ai.berkeley.edu.
# 
# Attribution Information: The Pacman AI projects were developed at UC Berkeley.
# The core projects and autograders were primarily created by John DeNero
# (denero@cs.berkeley.edu) and Dan Klein (klein@cs.berkeley.edu).
# Student side autograding was added by Brad Miller, Nick Hay, and
# Pieter Abbeel (pabbeel@cs.berkeley.edu).


from util import manhattanDistance
from game import Directions
import random, util

from game import Agent

class ReflexAgent(Agent):
    """
      A reflex agent chooses an action at each choice point by examining
      its alternatives via a state evaluation function.

      The code below is provided as a guide.  You are welcome to change
      it in any way you see fit, so long as you don't touch our method
      headers.
    """


    def getAction(self, gameState):
        """
        You do not need to change this method, but you're welcome to.

        getAction chooses among the best options according to the evaluation function.

        Just like in the previous project, getAction takes a GameState and returns
        some Directions.X for some X in the set {North, South, West, East, Stop}
        """
        # Collect legal moves and successor states
        legalMoves = gameState.getLegalActions()

        # Choose one of the best actions
        scores = [self.evaluationFunction(gameState, action) for action in legalMoves]
        bestScore = max(scores)
        bestIndices = [index for index in range(len(scores)) if scores[index] == bestScore]
        chosenIndex = random.choice(bestIndices) # Pick randomly among the best

        "Add more of your code here if you want to"

        return legalMoves[chosenIndex]

    def evaluationFunction(self, currentGameState, action):
        """
        Design a better evaluation function here.

        The evaluation function takes in the current and proposed successor
        GameStates (pacman.py) and returns a number, where higher numbers are better.

        The code below extracts some useful information from the state, like the
        remaining food (newFood) and Pacman position after moving (newPos).
        newScaredTimes holds the number of moves that each ghost will remain
        scared because of Pacman having eaten a power pellet.

        Print out these variables to see what you're getting, then combine them
        to create a masterful evaluation function.
        """
        # Useful information you can extract from a GameState (pacman.py)
        successorGameState = currentGameState.generatePacmanSuccessor(action)
        newPos = successorGameState.getPacmanPosition()
        newFood = successorGameState.getFood()
        newGhostStates = successorGameState.getGhostStates()
        newScaredTimes = [ghostState.scaredTimer for ghostState in newGhostStates]

        "*** YOUR CODE HERE ***"
        score = successorGameState.getScore()

        ghostdistance = manhattanDistance(newPos, newGhostStates[0].getPosition())
        if ghostdistance != 0:
          score -= 15.0 / ghostdistance

        fooddistances = []
        for food in newFood.asList():
          distance = manhattanDistance(newPos, food)
          fooddistances.append(distance)
        if len(fooddistances):
          score += 10.0 / min(fooddistances)


        return score

def scoreEvaluationFunction(currentGameState):
    """
      This default evaluation function just returns the score of the state.
      The score is the same one displayed in the Pacman GUI.

      This evaluation function is meant for use with adversarial search agents
      (not reflex agents).
    """
    return currentGameState.getScore()

class MultiAgentSearchAgent(Agent):
    """
      This class provides some common elements to all of your
      multi-agent searchers.  Any methods defined here will be available
      to the MinimaxPacmanAgent, AlphaBetaPacmanAgent & ExpectimaxPacmanAgent.

      You *do not* need to make any changes here, but you can if you want to
      add functionality to all your adversarial search agents.  Please do not
      remove anything, however.

      Note: this is an abstract class: one that should not be instantiated.  It's
      only partially specified, and designed to be extended.  Agent (game.py)
      is another abstract class.
    """

    def __init__(self, evalFn = 'scoreEvaluationFunction', depth = '2'):
        self.index = 0 # Pacman is always agent index 0
        self.evaluationFunction = util.lookup(evalFn, globals())
        self.depth = int(depth)



class MinimaxAgent(MultiAgentSearchAgent):
    """
      Your minimax agent (question 2)
    """

    def getAction(self, gameState):
        """
          Returns the minimax action from the current gameState using self.depth
          and self.evaluationFunction.

          Here are some method calls that might be useful when implementing minimax.

          gameState.getLegalActions(agentIndex):
            Returns a list of legal actions for an agent
            agentIndex=0 means Pacman, ghosts are >= 1

          gameState.generateSuccessor(agentIndex, action):
            Returns the successor game state after an agent takes an action

          gameState.getNumAgents():
            Returns the total number of agents in the game

          gameState.isWin():
            Returns whether or not the game state is a winning state

          gameState.isLose():
            Returns whether or not the game state is a losing state
        """
        "*** YOUR CODE HERE ***"
        def minmax(state, depth, agent):
          successors = []
          if agent == state.getNumAgents():
            depth += 1
            return minmax(state, depth, 0)

          terminal = self.depth == depth or state.isWin() or state.isLose() or state.getLegalActions(agent) == 0
          if terminal:
            return self.evaluationFunction(state)
          for action in state.getLegalActions(agent):
            successors.append(minmax(state.generateSuccessor(agent, action), depth, agent + 1))
          if agent % state.getNumAgents() == 0:
            return max(successors)
          else:
            return min(successors)
        actions = gameState.getLegalActions(0)
        best = actions[0]
        for action in actions:
          if minmax(gameState.generateSuccessor(0, action), 0, 1) > minmax(gameState.generateSuccessor(0, best), 0, 1):
            best = action
        return best




class AlphaBetaAgent(MultiAgentSearchAgent):
    """
      Your minimax agent with alpha-beta pruning (question 3)
    """

    def getAction(self, gameState):
        decision = self.minimax_alpha_beta(gameState, self.index, 0, float('-infinity'), float('infinity'))
        move = decision[1]
        return move

    def minimax_alpha_beta(self, state, depth, agent, alpha, beta):
        num_agents = state.getNumAgents()

        if agent % num_agents == 0 and depth == self.depth:
            return [self.evaluationFunction(state), None]

        elif agent % num_agents == 0:
            return self.maximize_alpha_beta(state, depth, agent % num_agents, alpha, beta)

        return self.minimize_alpha_beta(state, depth, agent % num_agents,  alpha, beta)

    def minimize_alpha_beta(self, state, depth, agent, alpha, beta):
        actions = state.getLegalActions(agent)

        if len(actions) == 0:
            return [self.evaluationFunction(state), None]

        score = float('infinity')
        best_action = None

        for action in actions:
            successor_state = state.generateSuccessor(agent, action)
            decision = self.minimax_alpha_beta(successor_state, depth, agent + 1, alpha, beta)
            new_value = decision[0]
            if new_value < score:
                score = new_value
                best_action = action
            if score < alpha:
                return [score, best_action]
            beta = min(beta, score)

        return [score, best_action]

    def maximize_alpha_beta(self, state, depth, agent, alpha, beta):
        actions = state.getLegalActions(agent)
        if len(actions) == 0:
            return [self.evaluationFunction(state), None]
        score = float('-infinity')
        best_action = None
        for action in actions:
            successor_state = state.generateSuccessor(agent, action)
            decision = self.minimax_alpha_beta(successor_state, depth + 1, agent + 1, alpha, beta)
            if decision[0] > score:
                score = decision[0]
                best_action = action
            if score > beta:
                return [score, best_action]
            alpha = max(alpha, score)

        return [score, best_action]




class ExpectimaxAgent(MultiAgentSearchAgent):
    """
      Your expectimax agent (question 4)
    """

    def expectimax(self,depth, state,ghost):
      if state.isWin() or state.isLose() or depth == self.depth:
        return self.evaluationFunction(state)
      actions = state.getLegalActions(ghost)
      successors = []
      scores = []
      for action in actions:
        successors.append(state.generateSuccessor(ghost, action))
      if ghost == state.getNumAgents() - 1:
        for successor in successors:
          scores.append(self.maximizer(depth + 1, successor))
      elif ghost < state.getNumAgents() - 1:
        for successor in successors:
          scores.append(self.expectimax(depth, successor, ghost + 1))
      return sum(scores)/len(scores)
      
      
      return sum(scores)/len(scores)
    def maximizer(self, depth, state):
      if state.isWin() or state.isLose() or depth == self.depth:
        return self.evaluationFunction(state)
      actions  = state.getLegalActions(0)
      scores = []
      successors = []
      for action in actions:
        successors.append(state.generateSuccessor(0, action))
      for successor in successors:
        scores.append(self.expectimax(depth, successor, 1))
      return max(scores)

    def getAction(self, state):
      actions = state.getLegalActions(0)
      successors = []
      scores = []
      for action in actions:
        successors.append(state.generateSuccessor(0, action))
      for successor in successors:
        scores.append(self.expectimax(0, successor, 1))
      maxscore = max(scores)
      best = []
      for i in range(len(scores)):
        if scores[i] == maxscore:
          index = i
      return actions[index]


def betterEvaluationFunction(currentGameState):
    """
        Your extreme ghost-hunting, pellet-nabbing, food-gobbling, unstoppable
        evaluation function (question 5).
        DESCRIPTION: <Try to keep track of how close ghosts are as well as food as well as your score>
    """
    position = currentGameState.getPacmanPosition()
    ghoststates = currentGameState.getGhostStates()
    food = currentGameState.getFood()

    nearby_food = 0
    x_position = position[0]
    y_position = position[1]

    for x in range(x_position - 2, x_position + 3):
      for y in range(y_position - 2, y_position + 3):
        if x < len(list(food)) and x >=0 and y < len(list(food[1])) and food[x][y] and y >= 0:
          nearby_food += 1

    food_distances = []
    for x in range(0, len(food[0])):
        for y in range(0, len(food[x])):
            if food[x][y]:
               food_distances.append(manhattanDistance(position, (x,y)))
    if len(food_distances) > 0 and sum(food_distances) != 0:
      food_distance = sum(food_distances)/float(len(food_distances)) 
    else:
      food_distance = 1
    ghost_distances = []
    for ghoststate in ghoststates:
      ghost = ghoststate.getPosition()
      ghost_distances.append(manhattanDistance(ghost, position))
    if len(ghost_distances) > 0 and min(ghost_distances) != 0:
      ghost_distance = min(ghost_distances)
    else:
      ghost_distance = 1

    return currentGameState.getScore() + nearby_food + 10.0/food_distance + 15.0/ghost_distance 
# Abbreviation
better = betterEvaluationFunction

