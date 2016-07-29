package me.hii488;

import java.util.ArrayList;

import me.hii488.AISettings.GenerationSettings;
import me.hii488.NeuralNetwork.Child;

public class GeneticAlgorithm {
	public NeuralNetwork neuralNet;
	
	public GenerationSettings settings;
	
	public ArrayList<Child> children = new ArrayList<Child>();
	
	// TODO: Switch all the for loops to foreach loops
	public void makeRandomGeneration(){
		ArrayList<Child> childPool = new ArrayList<Child>();
		
		for(int i = 0; i < settings.childrenPerGeneration; i++){
			Child child = neuralNet.new Child();
			
			for(int layer = 0; layer < child.layers.length; layer++){
				for(int node = 0; node < child.layers[layer].nodes.length; node++){
					for(int weight = 0; weight < child.layers[layer].nodes[node].weights.length; weight++){
						child.layers[layer].nodes[node].weights[weight] = AISettings.rand.nextFloat() - AISettings.rand.nextFloat();
					}
				}
			}
			
			childPool.add(child);
		}
		children = childPool;
	}
	
	public void nextGeneration(){
		ArrayList<Child> childPool = new ArrayList<Child>();
		
		Child parentA;
		Child parentB;
		for(int i = 0; i < settings.childrenPerGeneration; i++){
			parentA = rouletteChoice(children);
			parentB = rouletteChoice(children);
			
			Child child = spliceChildren(parentA, parentB);
			child = mutateChild(child);
			childPool.add(child);
		}
		
		@SuppressWarnings("unchecked")
		ArrayList<Child> childPool2 = this.fitnessSortedChildren((ArrayList<Child>) children.clone());
		
		for(int i = settings.additionalTopChildrenKept -1; i >= 0; i--)
			childPool.add(childPool2.get(childPool2.size() - 1 - i));
		
		children = childPool;
	}
	
	
	public Child rouletteChoice(ArrayList<Child> children){
		
		int totalFitness = 0;
		for(int i = 0; i < children.size(); i++){
			totalFitness += children.get(i).fitness;
		}
		
		double value = AISettings.rand.nextDouble() * totalFitness;
		
		for(int i = 0; i < children.size(); i++){
			value -= children.get(i).fitness;
			if(value <= 0) return children.get(i);
		}
		
		return children.get(children.size()-1);
	}
	
	public Child spliceChildren(Child a, Child b){
		Child child = a.clone();
		
		for(int i = 0; i < child.layers.length; i++){
			for(int j = 0; j < child.layers[i].nodes.length; j++){
				final int crosspoint = AISettings.rand.nextInt(i == 0 ? neuralNet.settings.inputs : neuralNet.settings.nodesPerLayer[i-1]);
				for(int k = 0; k < child.layers[i].nodes[j].weights.length; k++){
					if(k >= crosspoint){
						child.layers[i].nodes[j].weights[k] = b.layers[i].nodes[j].weights[k];
					}
				}
			}
		}
		
		return child;
	}
	
	public Child mutateChild(Child child){
		for(int i = 0; i < child.layers.length; i++){
			for(int j = 0; j < child.layers[i].nodes.length; j++){
				for(int k = 0; k < child.layers[i].nodes[j].weights.length; k++){
					if(AISettings.rand.nextFloat() <= settings.mutationChance){
						child.layers[i].nodes[j].weights[k] += (AISettings.rand.nextBoolean() == true) ? (AISettings.rand.nextFloat()/2) * -1 : AISettings.rand.nextFloat()/2;
					}
				}
			}
		}
		return child;
	}
	
	// Not strictly necessary
	public float[] getOutputs(float[] inputs, int child){
		return recurrentOutput(inputs, child, 1);
	}
	
	public float[] recurrentOutput(float[] inputs, int child, int layer){
		float[] output = new float[neuralNet.settings.nodesPerLayer[layer]];
		
		for(int i = 0; i < neuralNet.settings.nodesPerLayer[layer]; i++){
			output[i] = children.get(child).layers[layer].nodes[i].activated(inputs);
		}
		
		return layer < neuralNet.settings.nodesPerLayer.length-1 ? recurrentOutput(output, child, layer+1) : output;
	}
	
	
	public ArrayList<Child> fitnessSortedChildren(ArrayList<Child> children2) {
	/*	ArrayList<Child> childPool = new ArrayList<Child>();
		int size = children2.size();
		for(int i = 0; i < size; i++){
			Child c = children2.get(0);
			int index = 0;
			for(int j = 0; j < children2.size(); j++){
				if(children2.get(j).fitness > c.fitness){
					index = j;
					c = children2.get(j);
				}
			}
			childPool.add(c);
			children2.remove(index);
		}
		return childPool;*/
		
		int i,j;

		for (i = 1; i < children2.size(); i++) {
			Child c = children2.get(i);
			j = i;
			while((j > 0) && (children2.get(j - 1).fitness > c.fitness)) {
				children2.set(j,children2.get(j - 1));
				j--;
			}
			children2.set(j,c);
		}
		    
		return children2;
	}
	
}